package com.hfad.multipleactivities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private val ADD_WORD_CODE = 1931

    private var wordToDefn = HashMap<String, String>()
    private val words = ArrayList<String>()
    private val defns = ArrayList<String>()
    private lateinit var myAdapter : ArrayAdapter<String>
    private var word = "a word"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val reader = Scanner (resources.openRawResource(R.raw.grewords))
        readDictionaryFile(reader)
        val reader2 = Scanner (openFileInput("extrawords.txt"))
        readDictionaryFile(reader2)
        setupList()

        definitions_list.setOnItemClickListener { _, _, index, _ ->
            if(wordToDefn[word] == defns[index]) {
                Toast.makeText(this, "Correct!", Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(this, "Wrong!", Toast.LENGTH_LONG).show()
            }
            setupList()
        }
    }

    private fun readDictionaryFile(reader: Scanner) {
        while(reader.hasNextLine()) {
            val line = reader.nextLine()
            val pieces = line.split("-")
            if (pieces.size >= 2) {
                words.add(pieces[0])
                wordToDefn.put(pieces[0], pieces[1])
            }
        }
    }

    private fun setupList() {


        // pick a random word

        val rand = Random()
        val index = rand.nextInt(words.size)
        word = words[index]
        the_word.text = word
        // pick random definitions for the word
        defns.clear()
        defns.add(wordToDefn[word]!!)
        words.shuffle()
        for (otherWord in words.subList(0, 5)) {
            if (otherWord == word || defns.size == 5) {
                continue
            }
            defns.add(wordToDefn[otherWord]!!)
        }
        defns.shuffle()

        myAdapter = ArrayAdapter<String>(this,
            android.R.layout.simple_expandable_list_item_1, defns)


        definitions_list.adapter = myAdapter

    }

    fun addWordButtonClick(view: View) {
        // launch the AddWordActivity
        val myIntent = Intent(this, AddWordActivity::class.java)
        startActivityForResult(myIntent, ADD_WORD_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, myIntent: Intent?) {
        if (requestCode == ADD_WORD_CODE) {
            //unpack the word and definition sent back from AddWordActivity
            if (myIntent != null) {
                val new_word = myIntent.getStringExtra("word")
                val new_defn = myIntent.getStringExtra("defn")
                wordToDefn[new_word] = new_defn
                words.add(new_word)
            }
        }
    }
}
