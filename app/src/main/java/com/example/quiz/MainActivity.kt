package com.example.quiz

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader

data class Question(val question: String, val options: List<String>, val answer: String)

class MainActivity : AppCompatActivity() {
    private lateinit var questionTextView: TextView
    private lateinit var optionsRadioGroup: RadioGroup
    private lateinit var submitButton: Button
    private lateinit var resultTextView: TextView
    private lateinit var restartButton: Button

    private lateinit var questions: List<Question>
    private var currentQuestionIndex = 0
    private var score = 0
    private var isQuizFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        questionTextView = findViewById(R.id.questionTextView)
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup)
        submitButton = findViewById(R.id.submitButton)
        resultTextView = findViewById(R.id.resultTextView)
        restartButton = findViewById(R.id.restartButton)

        loadQuestions()
        showQuestion()

        submitButton.setOnClickListener {
            if (!isQuizFinished) {
                checkAnswer()
            } else {
                // Ha vége a quiznek, ne lehessen többször rányomni
                resetQuiz()
            }
        }

        restartButton.setOnClickListener {
            resetQuiz()
        }
    }

    private fun loadQuestions() {
        val inputStream = assets.open("data.json")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val gson = Gson()
        val questionType = object : TypeToken<List<Question>>() {}.type
        questions = gson.fromJson(reader, questionType)
        reader.close()
    }

    private fun showQuestion() {
        val question = questions[currentQuestionIndex]
        questionTextView.text = question.question
        optionsRadioGroup.removeAllViews()

        for (option in question.options) {
            val radioButton = RadioButton(this)
            radioButton.text = option
            optionsRadioGroup.addView(radioButton)
        }

        resultTextView.visibility = View.GONE
        submitButton.isEnabled = true
        submitButton.text = if (currentQuestionIndex < questions.size - 1) "Következő" else "Befejezés"
    }

    private fun checkAnswer() {
        val selectedId = optionsRadioGroup.checkedRadioButtonId
        if (selectedId != -1) {
            val selectedOption = findViewById<RadioButton>(selectedId)
            val correctAnswer = questions[currentQuestionIndex].answer

            if (selectedOption.text == correctAnswer) {
                score++
                resultTextView.text = "Helyes válasz!"
            } else {
                resultTextView.text = "Hibás válasz! A helyes válasz: $correctAnswer"
            }

            resultTextView.visibility = View.VISIBLE

            // Ha van még kérdés, megyünk tovább, ha nincs, akkor befejezzük a quizt
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                showQuestion() // Következő kérdés megjelenítése
            } else {
                showScore() // Vége a quiznek
                isQuizFinished = true
                submitButton.text = "Újra"
            }
        } else {
            resultTextView.text = "Kérlek, válassz egy lehetőséget!"
            resultTextView.visibility = View.VISIBLE
        }
    }

    private fun showScore() {
        resultTextView.text = "Összpontszámod: $score/${questions.size}"
        resultTextView.visibility = View.VISIBLE
        submitButton.isEnabled = true // Az újrakezdés engedélyezése
    }

    private fun resetQuiz() {
        currentQuestionIndex = 0
        score = 0
        isQuizFinished = false
        submitButton.text = "Következő"
        showQuestion() // Vissza az első kérdéshez
    }
}
