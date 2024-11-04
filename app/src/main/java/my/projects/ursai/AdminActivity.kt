package my.projects.ursai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore
    private lateinit var container: LinearLayout
    private lateinit var addPlanLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin)

        supportActionBar?.hide()

        db = FirebaseFirestore.getInstance()
        container = findViewById(R.id.container)

        addPlanLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val title = data?.getStringExtra("title")
                val description = data?.getStringExtra("description")
                val price = data?.getStringExtra("price")
                val documentId = data?.getStringExtra("documentId")

                if (documentId != null) {
                    updatePlanView(documentId, title, description, price)
                } else {
                    if (title != null && description != null && price != null) {
                        addPlanToDatabase(title, description, price)
                    }
                }
            }
        }

        val btnAddNewPlan: Button = findViewById(R.id.btnAddNewPlan)
        btnAddNewPlan.setOnClickListener {
            val intent = Intent(this, AdminAddActivity::class.java)
            addPlanLauncher.launch(intent)
        }

        loadPlans()
    }

    private fun loadPlans() {
        db.collection("TB_PLANO")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val title = document.getString("nm_plano") ?: "Título não disponível"
                    val description = document.getString("descricao") ?: "Descrição não disponível"
                    val price = document.getString("preco") ?: "Preço não disponível"
                    val documentId = document.id

                    addPlanView(title, description, price, documentId)
                }
            }
            .addOnFailureListener { e ->
                Log.w("AdminActivity", "Erro ao carregar planos", e)
                Toast.makeText(this, "Erro ao carregar planos: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveToAsyncStorage(key: String, value: String) {
        val sharedPreferences = getSharedPreferences("MyAsyncStorage", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    private fun loadFromAsyncStorage(key: String): String? {
        val sharedPreferences = getSharedPreferences("MyAsyncStorage", MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }

    private fun addPlanToDatabase(title: String, description: String, price: String) {
        val planData = hashMapOf(
            "nm_plano" to title,
            "descricao" to description,
            "preco" to price
        )

        db.collection("TB_PLANO")
            .add(planData)
            .addOnSuccessListener { documentReference ->
                val documentId = documentReference.id
                saveToAsyncStorage("latestDocumentId", documentId)
                addPlanView(title, description, price, documentId)
                Toast.makeText(this, "Plano adicionado com sucesso!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Log.w("AdminActivity", "Erro ao adicionar plano", e)
                Toast.makeText(this, "Erro ao adicionar plano: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun addPlanView(title: String, description: String, price: String, documentId: String? = null) {
        val planView = LayoutInflater.from(this).inflate(R.layout.plan_adm, container, false)

        planView.findViewById<TextView>(R.id.planTitleText).text = title
        planView.findViewById<TextView>(R.id.descricao_plano).text = description
        planView.findViewById<TextView>(R.id.preco_plano).text = price

        planView.findViewById<ImageView>(R.id.editPlanImage).setOnClickListener {
            val editDialog = EditPlanModal()
            val bundle = Bundle()
            bundle.putString("title", title)
            bundle.putString("description", description)
            bundle.putString("price", price)
            editDialog.arguments = bundle

            editDialog.setEditPlanListener(object : EditPlanModal.EditPlanListener {
                override fun onPlanEdited(title: String, description: String, price: String) {
                    if (documentId != null) {
                        updatePlanView(documentId, title, description, price)
                    }
                }
            })

            editDialog.show(supportFragmentManager, "EditPlanDialogFragment")
        }

        planView.findViewById<ImageView>(R.id.deletePlanImage).setOnClickListener {
            if (documentId != null) {
                deletePlan(documentId)
            } else {
                Toast.makeText(this, "Erro: ID do plano não encontrado.", Toast.LENGTH_SHORT).show()
            }
        }

        container.addView(planView)
    }

    private fun deletePlan(documentId: String) {
        db.collection("TB_PLANO").document(documentId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Plano deletado com sucesso!", Toast.LENGTH_SHORT).show()
                container.removeAllViews()
                loadPlans()
            }
            .addOnFailureListener { e ->
                Log.w("AdminActivity", "Erro ao deletar plano", e)
                Toast.makeText(this, "Erro ao deletar plano: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun updatePlanView(documentId: String, title: String?, description: String?, price: String?) {
        val updatedData = hashMapOf<String, Any>()
        title?.let { updatedData["nm_plano"] = it }
        description?.let { updatedData["descricao"] = it }
        price?.let { updatedData["preco"] = it }

        db.collection("TB_PLANO").document(documentId)
            .update(updatedData)
            .addOnSuccessListener {
                Toast.makeText(this, "Plano atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                container.removeAllViews()
                loadPlans()
            }
            .addOnFailureListener { e ->
                Log.w("AdminActivity", "Erro ao atualizar plano", e)
                Toast.makeText(this, "Erro ao atualizar plano: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
