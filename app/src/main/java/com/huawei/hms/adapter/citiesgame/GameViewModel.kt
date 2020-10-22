package com.huawei.hms.adapter.citiesgame

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList
import kotlin.random.Random

class GameViewModel : ViewModel() {

    private val _result = MutableLiveData<Result>()
    private val _cpuAnswer = MutableLiveData<String>()

    private val cities = ArrayList<String>()
    private val usedCities = ArrayList<String>()

    private lateinit var lastCpuAnswer: String

    val result: LiveData<Result> = _result
    val cpuAnswer: LiveData<String> = _cpuAnswer

    fun initialize(context: Context) {
        cities.clear()
        usedCities.clear()
        cities.addAll(context.resources.getStringArray(R.array.cities_array))

        lastCpuAnswer = cities[Random.nextInt(cities.size)]
        _cpuAnswer.postValue(lastCpuAnswer)
    }

    fun checkUserInput(input: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val lowerCased = input.toLowerCase(Locale.getDefault())
            if (usedCities.find { it == lowerCased } != null) {
                _result.postValue(Result.AlreadyUsed)
            } else if (lastCpuAnswer.getLastCharacter() == lowerCased[0]) {
                markUsed(lowerCased)
                if (cities.isEmpty()) {
                    _result.postValue(Result.UserWin)
                } else {
                    _result.postValue(Result.AnswerCorrect)

                    delay(ANSWER_DELAY)

                    val cpuAnswer = cities.find { it.first() == input.getLastCharacter() }
                    if (cpuAnswer != null) {
                        markUsed(cpuAnswer)
                        lastCpuAnswer = cpuAnswer
                        _cpuAnswer.postValue(cpuAnswer)
                    } else {
                        _result.postValue(Result.UserWin)
                    }
                }
            } else if (!cities.contains(lowerCased) && !usedCities.contains(lowerCased)) {
                _result.postValue(Result.AnswerIncorrect)
            }

        }
    }

    private fun markUsed(city: String) {
        cities.remove(city)
        usedCities.add(city)
    }

    private fun String.getLastCharacter() = when (this.lastOrNull()) {
        'ъ', 'ь', 'ы', 'ц' -> if (this.length >= 2) this[this.length - 2] else null
        else -> if (this.isNotEmpty()) this[this.length - 1] else null
    }

    companion object {
        private const val ANSWER_DELAY: Long = 500
    }

    sealed class Result {
        object UserWin : Result()
        object AnswerCorrect : Result()
        object AlreadyUsed : Result()
        object AnswerIncorrect : Result()
    }
}