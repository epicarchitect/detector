package kolmachikhin.alexander.detector.ui.answers

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kolmachikhin.alexander.detector.R
import kolmachikhin.alexander.detector.analyse.Answer
import kolmachikhin.alexander.detector.constants.TEST_QUESTIONS_COUNT
import kolmachikhin.alexander.detector.ui.base.adapters.lists.ModelListAdapter
import kotlinx.android.synthetic.main.answer_item.view.*
import java.util.concurrent.Executors

class AnswersListAdapter(list: ArrayList<Answer> = ArrayList(TEST_QUESTIONS_COUNT)) : RecyclerView.Adapter<AnswersListAdapter.Holder>() {

    var list = list
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var isMutable = true
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(LayoutInflater.from(parent.context).inflate(R.layout.answer_item, parent, false))

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(list[holder.adapterPosition])

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            with(view) {
                radioAnswers.setOnCheckedChangeListener { _, checkedId ->
                    list[adapterPosition] = when (checkedId) {
                        R.id.radioA -> Answer.A
                        R.id.radioB -> Answer.B
                        R.id.radioC -> Answer.C
                        R.id.radioD -> Answer.D
                        else -> Answer.NULL
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(model: Answer) = with(itemView) {
            tvNumber.text = "${adapterPosition + 1}."
            when (model) {
                Answer.A -> radioAnswers.check(R.id.radioA)
                Answer.B -> radioAnswers.check(R.id.radioB)
                Answer.C -> radioAnswers.check(R.id.radioC)
                Answer.D -> radioAnswers.check(R.id.radioD)
                else -> radioAnswers.clearCheck()
            }

            radioA.isEnabled = isMutable
            radioB.isEnabled = isMutable
            radioC.isEnabled = isMutable
            radioD.isEnabled = isMutable

            when (model) {
                Answer.A -> radioA.isEnabled = true
                Answer.B -> radioB.isEnabled = true
                Answer.C -> radioC.isEnabled = true
                Answer.D -> radioD.isEnabled = true
            }
        }
    }
}