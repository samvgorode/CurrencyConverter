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

    private var fromListDialog: Dialog? = null
    private var toListDialog: Dialog? = null
    val isAmountExpanded = ObservableBoolean(true)

    private val fromAdapter = SelectCurrencyAdapter {
        fromListDialog?.dismiss()
        toListDialog?.dismiss()
        from.set(it.key)
        fromIcon.set(it.value)
        isAmountExpanded.set(true)
    }

    private val toAdapter = SelectCurrencyAdapter {
        fromListDialog?.dismiss()
        toListDialog?.dismiss()
        to.set(it.key)
        toIcon.set(it.value)
        isAmountExpanded.set(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentMainBinding.inflate(inflater, container, false).apply {
        lifecycleOwner = this@MainFragment
        fragment = this@MainFragment
    }.also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fromAdapter.setItems(
            mutableMapOf(
                "DKK" to R.drawable.ic_currency_dkk_small,
                "EUR" to R.drawable.ic_currency_eur_small,
                "GBP" to R.drawable.ic_currency_gbp_small,
                "HUF" to R.drawable.ic_currency_huf_small,
                "NOK" to R.drawable.ic_currency_nok_small,
                "PLN" to R.drawable.ic_currency_pln_small,
                "RON" to R.drawable.ic_currency_ron_small,
                "SEK" to R.drawable.ic_currency_sek_small
            )
        )
        toAdapter.setItems(
            mutableMapOf(
                "DKK" to R.drawable.ic_currency_dkk_small,
                "EUR" to R.drawable.ic_currency_eur_small,
                "GBP" to R.drawable.ic_currency_gbp_small,
                "HUF" to R.drawable.ic_currency_huf_small,
                "NOK" to R.drawable.ic_currency_nok_small,
                "PLN" to R.drawable.ic_currency_pln_small,
                "RON" to R.drawable.ic_currency_ron_small,
                "SEK" to R.drawable.ic_currency_sek_small
            )
        )
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
        val calculatedAmount = (rate * amount).toString()
        convertedAmount.set(calculatedAmount)
    }

    private fun handleError(response: UiResponse.Error) {
        showProgress.set(false)
        Toast.makeText(requireContext(), response.message, Toast.LENGTH_LONG).show()
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
        val dialog = Dialog(requireContext())
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
        viewModel.convert(currencyFrom, currencyTo, amount)
    }
}
