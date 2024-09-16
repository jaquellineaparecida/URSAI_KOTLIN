package my.projects.ursai

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CadastroActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        setContentView(R.layout.cadastro)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val cadButton = findViewById<Button>(R.id.buttonCadastrar)

        val nomeEmpresaInput = findViewById<EditText>(R.id.nomeEmpresaInput)
        val tipoEmpresaInput = findViewById<EditText>(R.id.tipoEmpresaInput)
        val cnpjInput = findViewById<EditText>(R.id.cnpjInput)
        val emailInput = findViewById<EditText>(R.id.emailInput)
        val telefoneInput = findViewById<EditText>(R.id.telefoneInput)
        val senhaInput = findViewById<EditText>(R.id.senhaInput)

        cadButton.setOnClickListener {
            val nomeEmpresa = nomeEmpresaInput.text.toString()
            val tipoEmpresa = tipoEmpresaInput.text.toString()
            val cnpj = cnpjInput.text.toString()
            val email = emailInput.text.toString()
            val telefone = telefoneInput.text.toString()
            val senha = senhaInput.text.toString()

            if (nomeEmpresa.isEmpty() || tipoEmpresa.isEmpty() || cnpj.isEmpty() || email.isEmpty() || telefone.isEmpty() || senha.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, senha)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val userId = auth.currentUser?.uid

                        val empresa = hashMapOf(
                            "id_empresa" to userId,
                            "nome" to nomeEmpresa,
                            "tipo" to tipoEmpresa,
                            "cnpj" to cnpj,
                            "email" to email,
                            "telefone" to telefone
                        )

                        userId?.let {
                            db.collection("empresas").document(it)
                                .set(empresa)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show()

                                    val intent = Intent(this, NoPlanActivity::class.java)
                                    startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, "Erro ao cadastrar empresa: ${e.message}", Toast.LENGTH_SHORT).show()
                                    Log.e("CadastroActivity", "Erro ao salvar dados no Firestore: ", e)
                                }
                        }
                    } else {
                        Toast.makeText(this, "Erro ao cadastrar o usuário: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        Log.e("CadastroActivity", "Erro ao criar usuário: ", task.exception)
                    }
                }
        }

    }

}