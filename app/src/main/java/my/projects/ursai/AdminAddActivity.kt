package my.projects.ursai

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class AdminAddActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_add)

        db = FirebaseFirestore.getInstance()

        val nomePlanoInput = findViewById<EditText>(R.id.nomePlanoInput)
        val descricaoInput = findViewById<EditText>(R.id.descricaoInput)
        val precoInput = findViewById<EditText>(R.id.precoInput)
        val buttonCadastrar = findViewById<Button>(R.id.buttonCadastrar)

        buttonCadastrar.setOnClickListener {
            val nomePlano = nomePlanoInput.text.toString().trim()
            val descricao = descricaoInput.text.toString().trim()
            val preco = precoInput.text.toString().trim()

            if (nomePlano.isEmpty() || descricao.isEmpty() || preco.isEmpty()) {
                Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show()
            } else {
                val plano = hashMapOf(
                    "nm_plano" to nomePlano,
                    "descricao" to descricao,
                    "preco" to preco
                )

                db.collection("TB_PLANO")
                    .add(plano)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Plano adicionado com sucesso!", Toast.LENGTH_SHORT).show()

                        val resultIntent = Intent()
                        resultIntent.putExtra("title", nomePlano)
                        resultIntent.putExtra("description", descricao)
                        resultIntent.putExtra("price", preco)
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Erro ao adicionar plano: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
        }
    }
}


