package my.projects.ursai

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.DialogFragment

class EditPlanModal : DialogFragment() {

    private var listener: EditPlanListener? = null

    interface EditPlanListener {
        fun onPlanEdited(title: String, description: String, price: String)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setCancelable(true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.modal, container, false)

        val titleEditText: EditText = view.findViewById(R.id.editPlanTitle)
        val descriptionEditText: EditText = view.findViewById(R.id.editPlanDescription)
        val priceEditText: EditText = view.findViewById(R.id.editPlanPrice)
        val saveButton: Button = view.findViewById(R.id.buttonSave)

        // Preencher os campos se necessário
        arguments?.let {
            titleEditText.setText(it.getString("title"))
            descriptionEditText.setText(it.getString("description"))
            priceEditText.setText(it.getString("price"))
        }

        saveButton.setOnClickListener {
            val title = titleEditText.text.toString()
            val description = descriptionEditText.text.toString()
            val price = priceEditText.text.toString()

            listener?.onPlanEdited(title, description, price)
            dismiss()
        }

        return view
    }

    fun setEditPlanListener(listener: EditPlanListener) {
        this.listener = listener
    }
}
