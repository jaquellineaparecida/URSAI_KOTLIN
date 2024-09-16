package my.projects.ursai

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginPlanActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_plan)

        supportActionBar?.hide()

        // Recebendo os dados do plano do Intent
        val planName = intent.getStringExtra("nm_plano") ?: "Plano Desconhecido"
        val planDescription = intent.getStringExtra("descricao") ?: "Descrição não disponível"
        val planPrice = intent.getStringExtra("preco") ?: "Preço não disponível"

        // Atualizando TextViews com os dados do plano
        val planTitleText = findViewById<TextView>(R.id.planTitleText)
        val planDescriptionText = findViewById<TextView>(R.id.planDescriptionText)
        val planPriceText = findViewById<TextView>(R.id.planPriceText)

        planTitleText.text = planName
        planDescriptionText.text = planDescription
        planPriceText.text = planPrice

        val btnCancelar = findViewById<LinearLayout>(R.id.subscribeButton)
        btnCancelar.setOnClickListener {
            Toast.makeText(this, "Plano Cancelado", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, NoPlanActivity::class.java)
            startActivity(intent)
        }
    }
}
