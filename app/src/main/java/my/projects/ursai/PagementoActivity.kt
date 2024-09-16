package my.projects.ursai

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PagamentoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pagamento)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val nmCartaoInput = findViewById<EditText>(R.id.nomeCartaoInput)
        val nmrCartaoInput = findViewById<EditText>(R.id.nrmCartaoInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val dtValidadeInput = findViewById<EditText>(R.id.dataValidadeInput)
        val cvvInput = findViewById<EditText>(R.id.cvvInput)
        val btnPagar = findViewById<Button>(R.id.buttonFinalizar)

        val planText = findViewById<TextView>(R.id.planText)
        val planPrice = findViewById<TextView>(R.id.planPrice)

        // Recebendo os dados passados pela Intent
        val plan = intent.getStringExtra("nm_plano") ?: ""
        val price = intent.getStringExtra("preco") ?: ""

        // Atualizando os TextViews com o nome e o preço do plano
        planText.text = plan
        planPrice.text = price

        btnPagar.setOnClickListener {
            val nmCartao = nmCartaoInput.text.toString()
            val nmrCartao = nmrCartaoInput.text.toString()
            val email = emailInput.text.toString()
            val dtValidade = dtValidadeInput.text.toString()
            val cvv = cvvInput.text.toString()

            if (nmCartao.isEmpty() || nmrCartao.isEmpty() || email.isEmpty() || dtValidade.isEmpty() || cvv.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val userId = auth.currentUser?.uid

            if (userId != null) {
                val pagamento = hashMapOf(
                    "id_empresa" to userId,
                    "nome_cartao" to nmCartao,
                    "numero_cartao" to nmrCartao,
                    "email" to email,
                    "data_validade" to dtValidade,
                    "cvv" to cvv,
                    "plano" to plan,
                    "preco" to price
                )

                // Salvando o pagamento no Firestore
                db.collection("pagamento").document(userId)
                    .set(pagamento)
                    .addOnSuccessListener {
                        // Mapear o nome do plano para o nome do documento
                        val documentName = when (plan) {
                            "Plano Básico" -> "plano_basico"
                            "Plano Padrão" -> "plano_padrao"
                            "Plano Premium" -> "plano_premium"
                            else -> {
                                Log.e("PagamentoActivity", "Plano desconhecido: $plan")
                                Toast.makeText(this, "Plano não encontrado", Toast.LENGTH_SHORT).show()
                                return@addOnSuccessListener
                            }
                        }

                        // Carregar os dados do plano do Firestore antes de redirecionar
                        db.collection("TB_PLANO").document(documentName)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document != null && document.exists()) {
                                    val planoName = document.getString("nm_plano") ?: "Plano Desconhecido"
                                    val planoDescription = document.getString("descricao") ?: "Descrição não disponível"
                                    val planoPrice = document.getString("preco") ?: "Preço não disponível"

                                    // Redirecionando para a LoginPlanActivity com os dados do plano
                                    val intent = Intent(this, LoginPlanActivity::class.java).apply {
                                        putExtra("nm_plano", planoName)
                                        putExtra("descricao", planoDescription)
                                        putExtra("preco", planoPrice)
                                    }
                                    startActivity(intent)
                                } else {
                                    Toast.makeText(this, "Plano não encontrado", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(this, "Erro ao carregar o plano: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao processar o pagamento: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}