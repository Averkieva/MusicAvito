package com.example.core.ui.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.core.databinding.FragmentTrackListBinding

/**
 * Абстрактный базовый фрагмент, от которого наследуются `ApiTrackFragment` и `DownloadedTracksFragment`.
 * Позволяет управлять общими элементами UI, такими как RecyclerView, поиск и заголовок экрана.
 *
 * @param T Тип `ViewModel`, используемого во фрагменте.
 * @param A Тип адаптера `RecyclerView.Adapter`, используемого для списка треков.
 */
abstract class BaseTracksFragment<T : ViewModel, A : RecyclerView.Adapter<*>>(
    private val layoutId: Int
) : Fragment() {

    protected var _binding: FragmentTrackListBinding? = null
    protected val binding get() = _binding!!

    // ViewModel и адаптер должны быть реализованы в наследниках
    protected abstract val viewModel: T
    protected abstract val adapter: A

    protected abstract val screenTitle: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrackListBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        updateScreenTitle()
        setupRecyclerView()
        setupSearch()

        // Скрытие клавиатуры при касании вне полей ввода
        binding.root.setOnTouchListener { _, _ ->
            hideKeyboard()
            false
        }
    }

    /**
     * Добавляется `OnScrollListener`, который скрывает клавиатуру при прокрутке.
     */
    private fun setupRecyclerView() {
        binding.resultRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@BaseTracksFragment.adapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy != 0) hideKeyboard()
                }
            })
        }
    }

    /**
     * Настройка поля поиска.
     * - Отслеживает изменения текста и обновляет кнопку очистки.
     * - При очистке вызовет `onSearchCleared()`.
     */
    private fun setupSearch() {
        binding.searchInputEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val query = s.toString()
                binding.cancelButton.visibility =
                    if (query.isNotEmpty()) View.VISIBLE else View.GONE
                onSearchQueryChanged(query)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.cancelButton.setOnClickListener {
            binding.searchInputEditText.text.clear()
            onSearchCleared()
        }
    }

    /**
     * Метод вызывается при изменении запроса в поле поиска.
     * Может быть переопределён в наследниках для обработки поиска.
     */
    protected open fun onSearchQueryChanged(query: String) {}

    /**
     * Метод вызывается при очистке поиска.
     * Может быть переопределён в наследниках для дополнительных действий.
     */
    protected open fun onSearchCleared() {}

    /**
     * Скрывает клавиатуру, если она открыта.
     */
    private fun hideKeyboard() {
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    /**
     * Устанавливает заголовок экрана.
     */
    private fun updateScreenTitle() {
        binding.searchTextView.text = screenTitle
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

