package com.example.guessplayer

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.guessplayer.databinding.ActivityMainBinding
import android.view.View
import android.widget.Toast
import com.example.guessplayer.fragments.FragmentForChapter1
import com.example.guessplayer.fragments.FragmentForChapter2
import com.example.guessplayer.fragments.FragmentForChapter3
import com.example.guessplayer.fragments.FragmentForChapter4
import com.example.guessplayer.fragments.FragmentForChapter5
import com.example.guessplayer.fragments.FragmentsForChaptersPager


class MainActivity : AppCompatActivity() {
    // View Binding позволяет безопасно работать с элементами layout
    private lateinit var binding: ActivityMainBinding
    private var gamerNowProgress = mapOf<String, Int>()

    companion object {
        private const val MAX_LEVEL_CHAPTER_1 = 12
    }

    override fun onCreate(savedInstanceState: Bundle?) { // основной метод жизненного цикла Activity
        super.onCreate(savedInstanceState)

        setupBinding() // инициализация View Binding и установка макета layout
        setupViewPager() // настройка ViewPager с фрагментами глав

        createProgressFile()
        gamerNowProgress = readProgressFile()
    }

    private fun setupBinding() {
        // преобразование элементов макета в объекты
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root) // установка корневого view в качестве содержимого Activity
    }

    private fun setupViewPager() {
        val fragments = listOf( // список фрагментов глав
            FragmentForChapter1(),
            FragmentForChapter2(),
            FragmentForChapter3(),
            FragmentForChapter4(),
            FragmentForChapter5()
        )

        val adapter = FragmentsForChaptersPager( // адаптер для ViewPager
            fragments, // cписок фрагментов для отображения
            supportFragmentManager, // менеджер фрагментов для управления их жизненным циклом
            lifecycle // жизненный цикл активности для синхронизации с фрагментами
        )

        binding.pager.adapter = adapter // установка адаптера в ViewPager
    }

    private fun createProgressFile() {
        val fileName = "game_progress.txt"
        val defaultContent = """
            chapter1 0
            chapter2 0
            chapter3 0
        """.trimIndent()

        try {
            // проверка на существование файла
            val files = fileList()
            if (!files.contains(fileName)) {
                // cоздание файла с начальными значениями
                openFileOutput(fileName, MODE_PRIVATE).use { output ->
                    output.write(defaultContent.toByteArray())
                }
                Log.d("FileCreate", "File $fileName is created")
            } else {
                Log.d("FileCreate", "File $fileName already created")
            }
        } catch (e: Exception) {
            Log.e("FileCreate", "Error with creation file $fileName", e)
        }
    }

    fun readProgressFile(): Map<String, Int> {
        val progressMap = mutableMapOf<String, Int>()
        try {
            openFileInput("game_progress.txt").bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val parts = line.trim().split("\\s+".toRegex())
                    if (parts.size == 2) {
                        val chapter = parts[0]
                        val level = parts[1].toIntOrNull() ?: 0
                        progressMap[chapter] = level
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FileRead", "Error with reading file game_progress.txt", e)
        }
        return progressMap
    }

    // обработчики нажатия кнопки для перехода к главам
    fun goToChapter1(view: View){
        val chapter1Level = gamerNowProgress["chapter1"] ?: 0

        if (chapter1Level < MAX_LEVEL_CHAPTER_1) {
            val intent = Intent(this, ChapterDefaultActivity::class.java)

            intent.putExtra("currentChapter", 1)
            intent.putExtra("currentLvl", chapter1Level)
            intent.putExtra("filenameForFootballPlayersClubs",
                "clubs_chapter_1.txt")
            intent.putExtra("filenameForFootballPlayersTransferYears",
                "years_chapter_1.txt")
            intent.putExtra("filenameForgetFootballPlayersNames",
                "players_chapter_1.txt")
            intent.putExtra("filenameForGameProgress",
                "game_progress.txt")

            startActivity(intent)
        } else {
            showMessage("Chapter I is already finished")
        }
    }

    fun goToChapter2(view: View) {
    }

    fun goToChapter3(view: View) {
    }

    fun goToChapter4(view: View) {
    }

    fun goToChapter5(view: View) {
    }

    fun showMessage(message: String) {
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}
