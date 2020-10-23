package com.huawei.hms.adapter.citiesgame

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.toast
import java.util.*

class CitiesGameActivity : AppCompatActivity() {

    private val viewModel by viewModels<GameViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.cpuAnswer.observe(this) {
            cpuAnswer.text = it.toUpperCase(Locale.getDefault())
            ok.isEnabled = true
            textInput.isEnabled = true
            textInput.text?.clear()
        }

        viewModel.result.observe(this) {
            when (it) {
                GameViewModel.Result.UserWin -> toast(getString(R.string.game_state_win))
                GameViewModel.Result.AnswerCorrect -> toast(getString(R.string.game_state_answer_accepted))
                GameViewModel.Result.AnswerIncorrect -> inputLayout.error = getString(R.string.game_state_wrong_answer)
                GameViewModel.Result.AlreadyUsed -> inputLayout.error = getString(R.string.game_state_already_used)
            }
        }

        textInput.doOnTextChanged { text, _, _, _ ->
            ok.isEnabled = !text.isNullOrEmpty()
            inputLayout.error = null
        }

        ok.setOnClickListener {
            ok.isEnabled = false
            viewModel.checkUserInput(textInput.text.toString())
        }

        viewModel.initialize(this)
    }
}