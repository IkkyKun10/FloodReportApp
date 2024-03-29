package com.riezki.floodreportapp.ui.auth

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.riezki.floodreportapp.databinding.ActivityAuthBinding


class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        window?.decorView?.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        window.statusBarColor = Color.TRANSPARENT
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        WindowCompat.setDecorFitsSystemWindows(
//            window,
//            false
//        )



//        if (Build.VERSION.SDK_INT in 21..29) {
//            window.statusBarColor = Color.TRANSPARENT
//            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
//            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//            window.decorView.systemUiVisibility =
//                SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or SYSTEM_UI_FLAG_LAYOUT_STABLE
//
//        } else if (Build.VERSION.SDK_INT >= 30) {
////            // Root ViewGroup of my activity
////            //val root = findViewById<ConstraintLayout>(R.id.roots)
//            val root = binding.root
//
//            ViewCompat.setOnApplyWindowInsetsListener(root) { view, windowInsets ->
//
//                val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
//
//                // Apply the insets as a margin to the view. Here the system is setting
//                // only the bottom, left, and right dimensions, but apply whichever insets are
//                // appropriate to your layout. You can also update the view padding
//                // if that's more appropriate.
//
//                view.layoutParams = (view.layoutParams as FrameLayout.LayoutParams).apply {
//                    leftMargin = insets.left
//                    bottomMargin = insets.bottom
//                    rightMargin = insets.right
//                }
//
//                // Return CONSUMED if you don't want want the window insets to keep being
//                // passed down to descendant views.
//                WindowInsetsCompat.CONSUMED
//            }
//        }
    }
}