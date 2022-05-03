package com.transfergo.currencyconverter.presentation

import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.databinding.ObservableInt
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.transfergo.currencyconverter.R
import com.transfergo.currencyconverter.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal
import java.math.RoundingMode

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainFragmentViewModel by viewModels()
    private var binding: FragmentMainBinding? = null

    val showProgress = ObservableBoolean()
    val from = ObservableField("EUR")
    val fromIcon = ObservableInt(R.drawable.ic_currency_eur_small)
    val to = ObservableField("GBP")
    val toIcon = ObservableInt(R.drawable.ic_currency_gbp_small)
    val amount = ObservableField<String>()
    val convertedAmount = ObservableField<String>()
    val isAmountExpanded = ObservableBoolean(true)

    // select currency dialogs
    private var fromListDialog: Dialog? = null
    private var toListDialog: Dialog? = null

    // select currency adapters
    private val fromAdapter = SelectCurrencyAdapter(::fromCurrencyClick)
    private val toAdapter = SelectCurrencyAdapter(::toCurrencyClick)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMainBinding.inflate(inflater, container, false).apply {
        lifecycleOwner = this@MainFragment
        fragment = this@MainFragment
    }.also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillAdapters()
        viewModel.uiResponse.observe(this.viewLifecycleOwner) { response ->
            when (response) {
                UiResponse.Progress -> showProgress.set(true)
                is UiResponse.Success -> handleSuccess(response)
                is UiResponse.Error -> handleError(response)
            }
        }
    }

    private fun handleSuccess(response: UiResponse.Success) {
        showProgress.set(false)
        isAmountExpanded.set(false)
        val rate = response.rate ?: BigDecimal.ONE
        val amount = amount.get()?.toBigDecimalOrNull() ?: BigDecimal.ZERO
        val calculatedAmount = (rate * amount).setScale(2, RoundingMode.CEILING).toString()
        convertedAmount.set(calculatedAmount)
    }

    private fun handleError(response: UiResponse.Error) {
        showProgress.set(false)
        toast(response.message)
    }

    fun onFromClick() = binding?.from?.let {
        fromListDialog = getDialog(fromAdapter, it)
        fromListDialog?.show()
    }

    fun onToClick() = binding?.to?.let {
        toListDialog = getDialog(toAdapter, it)
        toListDialog?.show()
    }

    private fun getDialog(adapter: SelectCurrencyAdapter, linkToView: View): Dialog {
        val dialog = Dialog(requireContext(), R.style.DialogTheme)
        dialog.setContentView(R.layout.select_currency_list)
        dialog.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        val list = dialog.findViewById<RecyclerView>(R.id.recycler)
        list?.layoutParams?.width = linkToView.measuredWidth
        list?.adapter = adapter
        val layoutParams = dialog.window?.attributes
        layoutParams?.gravity = Gravity.TOP or Gravity.START
        val point = IntArray(2)
        linkToView.getLocationOnScreen(point)
        val (x, y) = point
        layoutParams?.x = x
        layoutParams?.y = y
        return dialog
    }

    fun convert() {
        val currencyFrom = from.get().orEmpty()
        val currencyTo = to.get().orEmpty()
        val amount = amount.get().orEmpty()
        if(amount.isBlank()) {
            toast(getString(R.string.amount_is_required))
            return
        }
        viewModel.convert(currencyFrom, currencyTo, amount)
    }

    fun switchCurrencies() {
        val to = to.get()
        val toIcon = toIcon.get()
        val from = from.get()
        val fromIcon = fromIcon.get()
        this.from.set(to)
        this.fromIcon.set(toIcon)
        this.to.set(from)
        this.toIcon.set(fromIcon)
        if(isAmountExpanded.get().not()) {
            val amount = amount.get()
            val convertedAmount = convertedAmount.get()
            this.amount.set(convertedAmount)
            this.convertedAmount.set(amount)
        }
        fillAdapters()
    }

    private fun fillAdapters() {
        val exclude = listOf(from.get().orEmpty(), to.get().orEmpty())
        val items = viewModel.getCurrencies(exclude)
        fromAdapter.setItems(items)
        toAdapter.setItems(items)
    }

    private fun toast(text: String) =
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()

    private fun fromCurrencyClick(entry: Pair<String, Int>) = entry.let {
        fromListDialog?.dismiss()
        toListDialog?.dismiss()
        from.set(it.first)
        fromIcon.set(it.second)
        isAmountExpanded.set(true)
        fillAdapters()
    }

    private fun toCurrencyClick(entry: Pair<String, Int>) = entry.let {
        fromListDialog?.dismiss()
        toListDialog?.dismiss()
        to.set(it.first)
        toIcon.set(it.second)
        isAmountExpanded.set(true)
        fillAdapters()
    }
}
