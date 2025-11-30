package com.example.guessplayer.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

// адаптер, который управляет отображением фрагментов глав
class FragmentsForChaptersPager(
    private val fragments: List<Fragment>, // список фрагментов, которые будут отображаться
    fragmentManager: FragmentManager, // менеджер фрагментов для управления их жизненным циклом
    lifecycle: Lifecycle // жизненный цикл для синхронизации с активностью
): FragmentStateAdapter( // передача параметров в родительский класс
    fragmentManager,
    lifecycle
) {

    override fun getItemCount(): Int{ // возвращает общее количество фрагментов в адаптере
        return fragments.size
    }

    // создает и возвращает фрагмент для указанной позиции
    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }
}