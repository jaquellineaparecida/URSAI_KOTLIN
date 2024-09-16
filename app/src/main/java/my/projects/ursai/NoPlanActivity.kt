package my.projects.ursai

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.FirebaseFirestore

class NoPlanActivity : AppCompatActivity() {

    private var selectedPlan: LinearLayout? = null
    private var selectedPlanName: String? = null
    private var selectedPlanPrice: String? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.no_plan)

        supportActionBar?.hide()

        // Referências aos containers dos planos
        val planBasic = findViewById<LinearLayout>(R.id.planContainerBasic)
        val planDefault = findViewById<LinearLayout>(R.id.planContainerDefault)
        val planPremium = findViewById<LinearLayout>(R.id.planContainerPremium)
        val payButton = findViewById<LinearLayout>(R.id.subscribeButton)

        // Listeners de clique para os planos
        planBasic.setOnClickListener { selectPlan("plano_basico") }
        planDefault.setOnClickListener { selectPlan("plano_padrao") }
        planPremium.setOnClickListener { selectPlan("plano_premium") }

        // Listener para o botão de pagamento
        payButton.setOnClickListener {
            if (selectedPlanName != null && selectedPlanPrice != null) {
                val intent = Intent(this, PagamentoActivity::class.java).apply {
                    putExtra("nm_plano", selectedPlanName)
                    putExtra("preco", selectedPlanPrice)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Por favor, selecione um plano antes de continuar.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun selectPlan(planId: String) {
        Toast.makeText(this, "Selecionando plano: $planId", Toast.LENGTH_SHORT).show()
        val docRef = db.collection("TB_PLANO").document(planId)

        docRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                // Obtendo os campos corretos do documento
                val planName = document.getString("nm_plano")
                val planPrice = document.getString("preco")

                // Resetando a seleção anterior
                selectedPlan?.background = ContextCompat.getDrawable(this, R.drawable.plan_border)

                // Atualizando o container selecionado
                selectedPlan = when (planId) {
                    "plano_basico" -> findViewById(R.id.planContainerBasic)
                    "plano_padrao" -> findViewById(R.id.planContainerDefault)
                    "plano_premium" -> findViewById(R.id.planContainerPremium)
                    else -> null
                }

                // Aplicando o estilo de seleção
                selectedPlan?.background = ContextCompat.getDrawable(this, R.drawable.border_select)

                // Atualizando os dados do plano selecionado
                selectedPlanName = planName
                selectedPlanPrice = planPrice

            } else {
                Toast.makeText(this, "Plano não encontrado.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { exception ->
            Toast.makeText(this, "Erro ao carregar plano: ${exception.message}", Toast.LENGTH_SHORT).show()
        }
    }
}