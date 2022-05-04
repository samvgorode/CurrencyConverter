package com.transfergo.currencyconverter.presentation

import android.app.Dialog
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
import com.transfergo.currencyconverter.data.local.LocalDataSource.Companion.EUR
import com.transfergo.currencyconverter.data.local.LocalDataSource.Companion.EUR_ICON
import com.transfergo.currencyconverter.data.local.LocalDataSource.Companion.GBP
import com.transfergo.currencyconverter.data.local.LocalDataSource.Companion.GBP_ICON
import com.transfergo.currencyconverter.databinding.FragmentMainBinding
import com.transfergo.currencyconverter.domain.Currency
import com.transfergo.currencyconverter.presentation.adapters.SelectCurrencyAdapter
import com.transfergo.currencyconverter.presentation.viewModel.MainFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.math.BigDecimal

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainFragmentViewModel by viewModels()
    private var binding: FragmentMainBinding? = null

    // observables
    val showProgress = ObservableBoolean()
    val from = ObservableField(EUR)
    val fromIcon = ObservableInt(EUR_ICON)
    val to = ObservableField(GBP)
    val toIcon = ObservableInt(GBP_ICON)
    val amount = ObservableField<String>()
    val convertedAmount = ObservableField<String>()
    val rateFullText = ObservableField<String>()
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
        observeLiveDate()
    }

    private fun observeLiveDate() {
        viewModel.uiState.observe(this.viewLifecycleOwner) { response ->
            when (response) {
                UiState.Progress -> showProgress.set(true)
                is UiState.Success -> handleSuccess(response)
                is UiState.Error -> handleError(response)
            }
        }
        viewModel.currencies.observe(this.viewLifecycleOwner) { currencies ->
            fromAdapter.setItems(currencies)
            toAdapter.setItems(currencies)
        }
    }

    private fun handleSuccess(response: UiState.Success) = response.lastKnownState?.let { state ->
        showProgress.set(false)
        isAmountExpanded.set(false)
        val rate = state.rate ?: BigDecimal.ONE
        rateFullText.set("1 ${from.get()} = $rate ${to.get()}")
        from.set(state.from)
        state.fromIcon?.let(fromIcon::set)
        to.set(state.to)
        state.toIcon?.let(toIcon::set)
        amount.set(state.fromAmount.toString())
        convertedAmount.set(state.toAmount.toString())
    }

    private fun handleError(response: UiState.Error) {
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

    fun convert() {
        val currencyFrom = from.get().orEmpty()
        val currencyTo = to.get().orEmpty()
        val amount = amount.get().orEmpty()
        if (amount.isBlank()) {
            toast(getString(R.string.amount_is_required))
            return
        }
        viewModel.convert(currencyFrom, currencyTo, amount)
    }

    fun switchCurrencies() {
        switchCurrency()
        switchIcon()
        if (isAmountExpanded.get().not()) {
            switchAmounts()
            hideRateHint()
        }
        fillAdapters()
    }

    private fun switchCurrency() {
        val to = to.get()
        val from = from.get()
        this.from.set(to)
        this.to.set(from)
    }

    private fun switchIcon() {
        val toIcon = toIcon.get()
        val fromIcon = fromIcon.get()
        this.fromIcon.set(toIcon)
        this.toIcon.set(fromIcon)
    }

    private fun switchAmounts() {
        val amount = amount.get()
        val convertedAmount = convertedAmount.get()
        this.amount.set(convertedAmount)
        this.convertedAmount.set(amount)
    }

    private fun hideRateHint() {
        rateFullText.set("")
    }

    private fun fillAdapters() {
        val exclude = listOf(from.get().orEmpty(), to.get().orEmpty())
        viewModel.getCurrencies(exclude)
    }

    private fun toast(text: String) =
        Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()

    private fun fromCurrencyClick(currency: Currency) = currency.run {
        fromListDialog?.dismiss()
        toListDialog?.dismiss()
        from.set(name)
        fromIcon.set(icon)
        isAmountExpanded.set(true)
        fillAdapters()
    }

    private fun toCurrencyClick(currency: Currency) = currency.run {
        fromListDialog?.dismiss()
        toListDialog?.dismiss()
        to.set(name)
        toIcon.set(icon)
        isAmountExpanded.set(true)
        fillAdapters()
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
}
