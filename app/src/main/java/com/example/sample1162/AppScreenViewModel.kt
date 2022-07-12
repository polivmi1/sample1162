package com.example.sample1162

import android.os.Parcelable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import java.util.*


open class OEffect {
    val id: Long = UUID.randomUUID().mostSignificantBits

    override fun toString(): String {
        return this.javaClass.name.replaceBeforeLast('$',"").substring(1)
    }
}

class AppScreenViewModel : ViewModel() {

    private val _state = MutableStateFlow(State())
    val state = _state.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), _state.value)

    @Parcelize
    data class State(
        val effects: List<Effect> = emptyList(),
    ) : Parcelable

    @Parcelize
    sealed class Effect: OEffect(), Parcelable {
        data class Navigate(val route: String) : Effect()
    }

    sealed class Event {
        data class DrawerItemClicked(val route: String) : Event()
        data class EffectHandled(val effect: Effect) : Event()
    }

    fun event(event: Event) {

        when (event) {
            is Event.DrawerItemClicked -> onDrawerItemClicked(event.route)
            is Event.EffectHandled -> onEffectHandled(event.effect.id)
        }
    }

    private fun onEffectHandled(id: Long) {
        _state.update {
            val effects = it.effects.filterNot { effect -> effect.id == id }
            it.copy(effects = effects)
        }
    }

    private fun onDrawerItemClicked(route: String) = viewModelScope.launch {
        _state.update { it.copy(effects = it.effects + Effect.Navigate(route = route)) }
    }
}