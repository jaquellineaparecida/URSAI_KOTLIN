package my.projects.ursai

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        supportActionBar?.hide()

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val loginButton: Button = findViewById(R.id.btn_login)
        val emailInput: EditText = findViewById(R.id.et_email)
        val passwordInput: EditText = findViewById(R.id.et_password)

        loginButton.setOnClickListener {
            val email = emailInput.text.toString()
            val password = passwordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = auth.currentUser?.uid
                            if (userId != null) {
                                // Verificar se o usuário tem um plano associado
                                db.collection("pagamento").document(userId)
                                    .get()
                                    .addOnSuccessListener { document ->
                                        if (document != null && document.exists()) {
                                            val planoName = document.getString("plano") ?: ""
                                            if (planoName.isNotEmpty()) {
                                                // Carregar detalhes do plano do Firestore
                                                db.collection("TB_PLANO").document(getDocumentName(planoName))
                                                    .get()
                                                    .addOnSuccessListener { planoDoc ->
                                                        if (planoDoc != null && planoDoc.exists()) {
                                                            val planoDescription = planoDoc.getString("descricao") ?: "Descrição não disponível"
                                                            val planoPrice = planoDoc.getString("preco") ?: "Preço não disponível"

                                                            // Redirecionar para a LoginPlanActivity com os detalhes do plano
                                                            val intent = Intent(this, LoginPlanActivity::class.java).apply {
                                                                putExtra("nm_plano", planoName)
                                                                putExtra("descricao", planoDescription)
                                                                putExtra("preco", planoPrice)
                                                            }
                                                            startActivity(intent)
                                                        } else {
                                                            Toast.makeText(this, "Detalhes do plano não encontrados", Toast.LENGTH_SHORT).show()
                                                            // Redirecionar para a NoPlanActivity
                                                            val intent = Intent(this, NoPlanActivity::class.java)
                                                            startActivity(intent)
                                                        }
                                                    }
                                                    .addOnFailureListener { e ->
                                                        Toast.makeText(this, "Erro ao carregar os detalhes do plano: ${e.message}", Toast.LENGTH_SHORT).show()
                                                        // Redirecionar para a NoPlanActivity
                                                        val intent = Intent(this, NoPlanActivity::class.java)
                                                        startActivity(intent)
                                                    }
                                            } else {
                                                // O usuário não tem um plano, redirecionar para a NoPlanActivity
                                                val intent = Intent(this, NoPlanActivity::class.java)
                                                startActivity(intent)
                                            }
                                        } else {
                                            // O usuário não tem um plano, redirecionar para a NoPlanActivity
                                            val intent = Intent(this, NoPlanActivity::class.java)
                                            startActivity(intent)
                                        }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(this, "Erro ao verificar o plano: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        } else {
                            Toast.makeText(this, "Erro ao fazer login: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getDocumentName(planoName: String): String {
        return when (planoName) {
            "Plano Básico" -> "plano_basico"
            "Plano Padrão" -> "plano_padrao"
            "Plano Premium" -> "plano_premium"
            else -> {
                "plano_desconhecido"
            }
        }
    }
}