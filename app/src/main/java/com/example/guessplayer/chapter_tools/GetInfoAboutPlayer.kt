package com.example.guessplayer.chapter_tools

import android.content.Context
import android.util.Log
import java.io.FileNotFoundException

class GetInfoAboutPlayer {

    companion object {
        // Добавляем параметр Context ко всем методам
        fun getFootballPlayersClubs(context: Context, filenameForFootballPlayersClubs: String): List<List<String>> {
            return try {
                context.assets.open(filenameForFootballPlayersClubs).bufferedReader().use { reader ->
                    reader.readLines().map { line ->
                        line.trim().split("\\s+".toRegex())
                    }
                }
            } catch (e: Exception) {
                Log.e("FileRead", "Error with reading file $filenameForFootballPlayersClubs", e)
                emptyList()
            }
        }

        fun getFootballPlayersTransferYears(context: Context, filenameForFootballPlayersTransferYears: String): List<List<String>> {
            return try {
                context.assets.open(filenameForFootballPlayersTransferYears).bufferedReader().use { reader ->
                    reader.readLines().map { line ->
                        line.trim().split("\\s+".toRegex())
                    }
                }
            } catch (e: Exception) {
                Log.e("FileRead", "Error with reading file $filenameForFootballPlayersTransferYears", e)
                emptyList()
            }
        }

        fun getFootballPlayersNames(context: Context, filenameForGetFootballPlayersNames: String): List<String> {
            return try {
                context.assets.open(filenameForGetFootballPlayersNames).bufferedReader().useLines { lines ->
                    lines.map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .toList()
                }
            } catch (e: Exception) {
                Log.e("FileRead", "Error with reading file: $filenameForGetFootballPlayersNames", e)
                emptyList()
            }
        }

        fun getCurrentLevelFromFile(context: Context, filenameForGameProgress: String, currentChapter: Int): Int {
            return try {
                context.openFileInput(filenameForGameProgress).bufferedReader().useLines { lines ->
                    lines.map { line ->
                        val parts = line.trim().split("\\s+".toRegex())
                        if (parts.size == 2 && parts[0] == "chapter$currentChapter") {
                            parts[1].toIntOrNull() ?: 0
                        } else {
                            0
                        }
                    }.firstOrNull() ?: 0
                }
            } catch (e: FileNotFoundException) {
                Log.e("FileRead", "File $filenameForGameProgress not found, creating default", e)
                // Создаем файл с начальным прогрессом
                updateProgressInFile(context, 1, currentChapter)
                1
            } catch (e: Exception) {
                Log.e("FileRead", "Error with reading file $filenameForGameProgress", e)
                1
            }
        }

        fun readProgressFile(context: Context): List<String> {
            return try {
                context.openFileInput("game_progress.txt").bufferedReader().useLines { it.toList() }
            } catch (e: FileNotFoundException) {
                // Если файла нет, создаем его с начальными значениями
                val initialContent = listOf("chapter1 1", "chapter2 1", "chapter3 1")
                updateProgressInFile(context, 1, 1)
                initialContent
            } catch (e: Exception) {
                Log.e("FileRead", "Error with reading file game_progress.txt", e)
                emptyList()
            }
        }

        fun updateProgressInFile(context: Context, newLevel: Int, currentChapter: Int) {
            val fileName = "game_progress.txt"

            try {
                val currentContent = readProgressFile(context)
                val updatedContent = mutableListOf<String>()

                var chapterFound = false
                for (line in currentContent) {
                    if (line.startsWith("chapter$currentChapter")) {
                        updatedContent.add("chapter$currentChapter $newLevel")
                        chapterFound = true
                    } else {
                        updatedContent.add(line)
                    }
                }

                // Если глава не найдена, добавляем новую запись
                if (!chapterFound) {
                    updatedContent.add("chapter$currentChapter $newLevel")
                }

                // Записываем обновленное содержимое
                context.openFileOutput(fileName, Context.MODE_PRIVATE).use { output ->
                    output.write(updatedContent.joinToString("\n").toByteArray())
                }

                Log.d("ProgressUpdate", "Progress updated: chapter$currentChapter $newLevel")

            } catch (e: Exception) {
                Log.e("ProgressUpdate", "Error with updating: chapter$currentChapter $newLevel", e)
            }
        }
    }
}