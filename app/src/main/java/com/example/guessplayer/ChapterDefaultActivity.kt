package com.example.guessplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEach
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.guessplayer.chapter_tools.FootballClub
import com.example.guessplayer.chapter_tools.FootballClubAdapter
import com.example.guessplayer.chapter_tools.GetInfoAboutPlayer
import com.example.guessplayer.chapter_tools.ResourceMapOfClubs
import kotlin.random.Random
import kotlin.text.replace
import kotlin.text.uppercase

open class ChapterDefaultActivity(): AppCompatActivity() {
    // кнопки
    lateinit var buttonToNextLvl: Button
    // кнопка перехода к следующему уровню после успешного ввода

    // лейауты
    lateinit var successWindow: RelativeLayout // структура окна успешного ввода слова
    lateinit var structureOfLetterButtons: LinearLayout // структура букв-кнопок
    // структура некликабельных кнопок для "визуализации" загаданного слова
    lateinit var structureOfButtonsForVisualization: LinearLayout
    lateinit var structureOfButtonsForInputField: LinearLayout // структура "поля ввода"
    private lateinit var recyclerView: RecyclerView

    // адаптеры
    private lateinit var footballClubAdapter: FootballClubAdapter

    // списки и словари
    private val listOfFreePositionsOnTop = Array(MAX_LETTER_BUTTONS) { 0 }
    private val listOfFreePositionsOnLow = Array(MAX_LETTER_BUTTONS) { 1 }
    private val listOfLetterButtonsForInputField = ArrayList<Button>()

    val listOfLetterButtons = ArrayList<Button>() // список букв-кнопок
    // список кнопок-букв в "поле ввода"
    var listOfFootballPlayersNames = ArrayList<String>() // список всех имен

    lateinit var listOfFootballPlayersClubs: List<List<String>> // список клубов
    lateinit var listOfFootballPlayersTransferYears: List<List<String>> // список дат трансферов
    private lateinit var listOfFootballClub: ArrayList<FootballClub>

    // числа
    companion object {
        private const val MAX_LETTER_BUTTONS = 17
        private const val MAX_LEVEL = 12

        private const val DEFAULT_WIDTH = 35
        private const val DEFAULT_HEIGHT = 55

        private const val SMALLER_WIDTH = 28
        private const val SMALLER_HEIGHT = 48

        private const val SMALLEST_WIDTH = 21
        private const val SMALLEST_HEIGHT = 45
    }
    var currentLvl = 0


    // Инициализация объектов классов
    val rmoc = ResourceMapOfClubs()
    val resourceMapOfClubs = rmoc.resourceMapOfClubs


    @SuppressLint("MissingInflatedId") // скип проверки на существование всех элементов
    override fun onCreate(savedInstanceState: Bundle?) { // основной метод жизненного цикла окна
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // режим полного покрытия экрана
        setContentView(R.layout.activity_default_chapter) // установка макета

        val intent = intent
        val currentChapter =
            intent.getIntExtra("currentChapter", 0) // номер действующей главы
        val filenameForFootballPlayersClubs =
            intent.getStringExtra("filenameForFootballPlayersClubs") ?: "null"
        val filenameForFootballPlayersTransferYears =
            intent.getStringExtra("filenameForFootballPlayersTransferYears") ?: "null"
        val filenameForGetFootballPlayersNames =
            intent.getStringExtra("filenameForgetFootballPlayersNames") ?: "null"
        val filenameForGameProgress =
            intent.getStringExtra("filenameForGameProgress") ?: "null"

        initializeObjects() // инициализация объектов активности

        initializeResources(
            filenameForGetFootballPlayersNames,
            filenameForFootballPlayersClubs,
            filenameForFootballPlayersTransferYears,
            filenameForGameProgress,
            currentChapter
        ) // инициализация ресурсов активности

        setLetterButtons()

        for (i in 0 until listOfLetterButtons.size) {
            resizeButton(listOfLetterButtons[i])
        }

        setupClickListeners() // отслеживание кликов
    }

    fun initializeResources(
        filenameForGetFootballPlayersNames: String,
        filenameForFootballPlayersClubs: String,
        filenameForFootballPlayersTransferYears: String,
        filenameForGameProgress: String,
        currentChapter: Int
    ) {
        listOfFootballPlayersNames =
            GetInfoAboutPlayer.getFootballPlayersNames(this,
                filenameForGetFootballPlayersNames) as ArrayList<String>
        listOfFootballPlayersClubs =
            GetInfoAboutPlayer.getFootballPlayersClubs(this,
                filenameForFootballPlayersClubs)
        listOfFootballPlayersTransferYears =
            GetInfoAboutPlayer.getFootballPlayersTransferYears(this,
                filenameForFootballPlayersTransferYears)

        currentLvl =
            GetInfoAboutPlayer.getCurrentLevelFromFile(this,
                filenameForGameProgress, currentChapter)

        listOfFootballClub = ArrayList()
        addDataToListOfFootballClub()

        footballClubAdapter = FootballClubAdapter(listOfFootballClub)
        recyclerView.adapter = footballClubAdapter
    }

    fun addDataToListOfFootballClub() {
        for (i in 0 until listOfFootballPlayersClubs[currentLvl].size) {
            val drawableId: Int = resourceMapOfClubs[listOfFootballPlayersClubs[currentLvl][i]]!!
            listOfFootballClub.add(
                FootballClub(
                    drawableId,
                    listOfFootballPlayersTransferYears[currentLvl][i]
                )
            )
        }
    }

    fun initializeObjects() { // инициализация объектов активности
        recyclerView = findViewById(R.id.recyclerview)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this,
            RecyclerView.HORIZONTAL, false)

        val buttonIds = listOf(
            R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5,
            R.id.button6, R.id.button7, R.id.button8, R.id.button9, R.id.button10,
            R.id.button11, R.id.button12, R.id.button13, R.id.button14, R.id.button15,
            R.id.button16, R.id.button17
        )
        listOfLetterButtons.addAll(buttonIds.map { findViewById(it) })

        buttonToNextLvl = findViewById(R.id.buttonNext)

        structureOfLetterButtons = findViewById(R.id.linearLayoutLow)
        structureOfButtonsForVisualization = findViewById(R.id.linearLayoutUp)
        structureOfButtonsForInputField = findViewById(R.id.linearLayoutUpTop)

        successWindow = findViewById(R.id.successWindowRelative)
    }

    fun setLetterButtons() {
        createInputField() // заполнение структуры "визуализации"
        val allLetters = generateStringForLetterButtons()

        repeat(MAX_LETTER_BUTTONS) { i ->
            listOfLetterButtons[i].text = allLetters[i].toString()
        }
    }

    fun createInputField() { // создание кнопки для визуализации "поля ввода"
        val name = listOfFootballPlayersNames[currentLvl]
        var width = DEFAULT_WIDTH
        var height = DEFAULT_HEIGHT
        var size = 2

        if (name.length > 9 && name.length < 13) {
            width = SMALLER_WIDTH
            height = SMALLER_HEIGHT
            size = 1
        }
        else if (name.length > 12) {
            width = SMALLEST_WIDTH
            height = SMALLEST_HEIGHT
            size = 0
        }

        for (i in 0 until name.length) {
            // создание кнопки со своим стилем под определенный символ
            if (name[i] == ' ') {
                createInputButton(0, i, width, height, size)
            }
            else if (name[i] == '-') {
                createInputButton(1, i, width, height, size)
            }
            else {
                createInputButton(2, i, width, height, size)
            }
        }
        setupClickListeners()
    }

    fun createInputButton(indicatorForStyle: Int,
                                  index: Int, width: Int, height: Int, size: Int) {
        val newButton = Button(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                width.dpToPx(),
                height.dpToPx(),
            ).apply {
                setMargins(
                    2.dpToPx(), 2.dpToPx(),
                    2.dpToPx(), 2.dpToPx()
                )
            }
            when (indicatorForStyle) {
                2 -> setBackgroundResource(R.drawable.button_background_input)
                0 -> {
                    if (index < listOfFreePositionsOnTop.size) {
                        listOfFreePositionsOnTop[index] = 1
                    }
                    setBackgroundResource(R.drawable.button_background_input_enter)
                }
                else -> {
                    if (index < listOfFreePositionsOnTop.size) {
                        listOfFreePositionsOnTop[index] = 1
                    }
                    setBackgroundResource(R.drawable.button_background_hyphen)
                }
            }
        }

        structureOfButtonsForVisualization.addView(newButton)
        newButton.isClickable = false

        if (indicatorForStyle == 0){
            newButton.text = " "
        }
        else if (indicatorForStyle == 1){
            newButton.text = "—"
            newButton.textSize = 21.0f
        }

        when (size) {
            0 -> newButton.textSize = 18.0f
        }

        // создание кнопки-заготовки для "поля ввода"
        createInvisibleButton(width, height, indicatorForStyle)
    }

    fun Int.dpToPx(): Int { // конвертирование dp в px
        return (this * resources.displayMetrics.density).toInt()
    }

    fun createInvisibleButton(width: Int, height: Int, cont: Int) {
        val newButton = Button(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                width.dpToPx(),
                height.dpToPx(),
            ).apply {
                setMargins(
                    2.dpToPx(), 2.dpToPx(),
                    2.dpToPx(), 2.dpToPx()
                )
            }
            setBackgroundResource(R.drawable.button_background_letters)
            setTextAppearance(R.style.CustomButtonStyle)
        }
        structureOfButtonsForInputField.addView(newButton)
        listOfLetterButtonsForInputField.add(newButton)

        newButton.isClickable = false
        newButton.visibility = View.INVISIBLE

        if (cont == 0){
            newButton.text = " "
        }
        else if (cont == 1){
            newButton.text = "—"
            newButton.textSize = 21.0f
        }
    }

    fun generateStringForLetterButtons(): String {
        val neededLetters = listOfFootballPlayersNames[currentLvl]
            .replace(" ", "")
            .replace("-", "")
            .uppercase()
        val lenToGenerate = MAX_LETTER_BUTTONS - neededLetters.length

        val allLetters = shuffleString(
            generateRandomLettersWithFrequency(lenToGenerate) + neededLetters
        )

        return allLetters
    }

    fun shuffleString(input: String): String {
        val chars = input.toMutableList()
        for (i in chars.size - 1 downTo 1) {
            val j = Random.nextInt(i + 1)
            val temp = chars[i]
            chars[i] = chars[j]
            chars[j] = temp
        }

        return chars.joinToString("")
    }

    fun generateRandomLettersWithFrequency(count: Int,
                                           excludeLetters: String = ""): String {
        if (count <= 0) return ""

        val letters = StringBuilder()
        val letterFrequency = mapOf(
            'E' to 12.7, 'T' to 9.1, 'A' to 8.2, 'O' to 7.5, 'I' to 7.0,
            'N' to 6.7, 'S' to 6.3, 'H' to 6.1, 'R' to 6.0, 'D' to 4.3,
            'L' to 4.0, 'C' to 2.8, 'U' to 2.8, 'M' to 2.4, 'W' to 2.4,
            'F' to 2.2, 'G' to 2.0, 'Y' to 2.0, 'P' to 1.9, 'B' to 1.5,
            'V' to 1.0, 'K' to 0.8, 'J' to 0.15, 'X' to 0.15, 'Q' to 0.10, 'Z' to 0.07
        )

        val availableLetters = letterFrequency.keys.filter { !excludeLetters.contains(it) }

        // список букв с учетом частотности
        val weightedLetters = mutableListOf<Char>()
        for (letter in availableLetters) {
            val frequency = letterFrequency[letter] ?: 1.0
            val count = (frequency * 2).toInt()
            repeat(count) {
                weightedLetters.add(letter)
            }
        }

        // перемешиваем список перед выбором
        weightedLetters.shuffle()

        // выбор случайных букв
        repeat(count) {
            if (weightedLetters.isNotEmpty()) {
                val randomChar = weightedLetters.removeAt(0) // удаление буквы после выбора
                letters.append(randomChar)
            }
        }

        return letters.toString()
    }

    fun resizeButton(button: Button) {
        button.layoutParams = LinearLayout.LayoutParams(
            DEFAULT_WIDTH.dpToPx(), DEFAULT_HEIGHT.dpToPx()
        ).apply {
            setMargins(
                4.dpToPx(), 4.dpToPx(),
                4.dpToPx(), 4.dpToPx()
            )
        }
    }

    fun setupClickListeners() { // обработчики нажатий
        repeat(MAX_LETTER_BUTTONS) { i ->
            listOfLetterButtons[i].setOnClickListener {
                handleLetterButtonClick(listOfLetterButtons[i], i) }
        }

        repeat(listOfLetterButtonsForInputField.size) { i ->
                listOfLetterButtonsForInputField[i].setOnClickListener {
                    handleLetterButtonClick(listOfLetterButtonsForInputField[i], i) }
        }

        buttonToNextLvl.setOnClickListener { hideRL() }
    }

    fun handleLetterButtonClick(button: Button, index: Int) { // клик на букву-кнопку
        // проверка на клик (из структуры букв-кнопок или из структуры "поля ввода"?)
        if (listOfLetterButtons.contains(button)) {
            // проверка на загруженность "поля ввода"
            if (listOfFootballPlayersNames[currentLvl].length > listOfFreePositionsOnTop.sum()) {
                moveButtonToUp(button, index) // "перемещает" букву-кнопку в "поле ввода"
            }
        }
        else {
            moveButtonToLow(button) // "перемещает" букву-кнопку из "поля ввода"
        }
    }

    fun moveButtonToUp(button: Button, index: Int) {
        val checkIn: Int = checkFreePosition()
        if (checkIn != -1) {
            button.visibility = View.INVISIBLE
            button.isClickable = false

            val buttonUp = structureOfButtonsForInputField.getChildAt(checkIn) as Button
            buttonUp.text = button.text
            buttonUp.isClickable = true

            buttonUp.visibility = View.VISIBLE

            // занимает позицию
            if (checkIn < listOfFreePositionsOnTop.size) {
                listOfFreePositionsOnTop[checkIn] = 1
            }
            if (index < listOfFreePositionsOnLow.size) {
                listOfFreePositionsOnLow[index] = 0
            }

            if (checkPositions() != -1) {
                showRL()

                val currentChapter = intent.getIntExtra("currentChapter", 1)
                GetInfoAboutPlayer.updateProgressInFile(this,
                    currentLvl + 1, currentChapter)
            }
        }
    }

    fun moveButtonToLow(button: Button) { // "перемещает" букву-кнопку из "поля ввода"
        button.visibility = View.INVISIBLE

        var buttonLow = button
        for (i in 0 until MAX_LETTER_BUTTONS) {
            if ((button.text == listOfLetterButtons[i].text) &&
                (listOfFreePositionsOnLow[i] == 0)) {

                buttonLow = listOfLetterButtons[i]
                listOfFreePositionsOnLow[i] = 1
                break
            }
        }

        button.text = ""
        buttonLow.visibility = View.VISIBLE
        buttonLow.isClickable = true

        // освобождает позицию
        val position = structureOfButtonsForInputField.indexOfChild(button)
        if (position != -1) {
            listOfFreePositionsOnTop[position] = 0
        }
    }

    fun checkFreePosition(): Int { // возвращает ближайшую к началу свободную позицию
        repeat(listOfFreePositionsOnTop.size) { i ->
            if (listOfFreePositionsOnTop[i] == 0) {
                return i
            }
        }
        return -1
    }

    fun checkPositions(): Int { // проверка на правильный ввод
        val nowName = listOfFootballPlayersNames[currentLvl].uppercase()

        repeat(nowName.length) { i ->
            val button = structureOfButtonsForInputField.getChildAt(i) as Button

            if (button.text != "—" && button.text != " ") {
                if (button.text != nowName[i].toString()) {
                    return -1
                }
            }
        }
        return 1
    }

    fun showRL() { // переход в видимый режим окна успешного ввода
        buttonToNextLvl.isClickable = true
        blockAllButtons()

        successWindow.visibility = View.VISIBLE
        successWindow.setAlpha(0f)

        successWindow.animate() // анимирование появление
            .alpha(1f)
            .setDuration(300)
            .setListener(null)
            .start()
    }

    private fun hideRL() { // переход в невидимый режим окна успешного ввода
        if (currentLvl == MAX_LEVEL) {
            endOfTheChapter()
            return
        }

        buttonToNextLvl.isClickable = false
        unblockAllButtons()

        currentLvl++
        clearWindow()
        setLetterButtons()
        reCreate()

        successWindow.animate() // анимирование исчезновение
            .alpha(0f)
            .setDuration(300)
            .withEndAction {
                successWindow.visibility = View.GONE
                successWindow.animate().setListener(null)
            }
            .start()
    }

    fun clearWindow() {
        structureOfButtonsForVisualization.removeAllViews()
        structureOfButtonsForInputField.forEach { moveButtonToLow(it as Button) }
        structureOfButtonsForInputField.removeAllViews()
    }

    fun blockAllButtons() { // переход букв-кнопок в некликабельный режим
        repeat(MAX_LETTER_BUTTONS) { i ->
            listOfLetterButtons[i].isClickable = false
        }
    }

    fun unblockAllButtons() { // переход букв-кнопок в кликабельный режим
        repeat(MAX_LETTER_BUTTONS) { i ->
            listOfLetterButtons[i].isClickable = true
        }
    }

    fun endOfTheChapter() {
        val message = "chapter passed"

        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT // или LENGTH_LONG
        ).show()

        findViewById<View>(android.R.id.content).postDelayed({
            finish()
        }, 500)
    }

    fun reCreate() {
        listOfFootballClub = ArrayList()
        addDataToList()

        footballClubAdapter = FootballClubAdapter(listOfFootballClub)
        recyclerView.adapter = footballClubAdapter
    }

    fun addDataToList() {
        for (i in 0 until listOfFootballPlayersClubs[currentLvl].size) {
            val drawableId: Int =
                rmoc.resourceMapOfClubs[listOfFootballPlayersClubs[currentLvl][i]]!!
            listOfFootballClub.add(
                FootballClub(
                    drawableId,
                    listOfFootballPlayersTransferYears[currentLvl][i]
                )
            )
        }
    }
}
