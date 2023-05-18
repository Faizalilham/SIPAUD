package android.coding.ourapp.presentation.ui


import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.coding.ourapp.R
import android.coding.ourapp.databinding.ActivityHomeBinding
import android.coding.ourapp.databinding.BottomSheetFilterBinding
import android.content.Intent
import android.os.Bundle
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog


class HomeActivity : AppCompatActivity() {

    private var _binding : ActivityHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomSheet()
        createCustomAnimation()

        binding.menuItem.setOnClickListener {
            startActivity(Intent(this,CreateUpdateAsesmentActivity::class.java))
        }

        binding.menuItemStudent.setOnClickListener {
            startActivity(Intent(this,StudentsActivity::class.java))
        }

        binding.imageProfile.setOnClickListener {
            startActivity(Intent(this,ProfileActivity::class.java))
        }

    }

    private fun createCustomAnimation() {
        val set = AnimatorSet()
        val scaleOutX = ObjectAnimator.ofFloat(binding.fabMenu.menuIconView, "scaleX", 1.0f, 0.2f)
        val scaleOutY = ObjectAnimator.ofFloat(binding.fabMenu.menuIconView, "scaleY", 1.0f, 0.2f)
        val scaleInX = ObjectAnimator.ofFloat(binding.fabMenu.menuIconView, "scaleX", 0.2f, 1.0f)
        val scaleInY = ObjectAnimator.ofFloat(binding.fabMenu.menuIconView, "scaleY", 0.2f, 1.0f)
        scaleOutX.duration = 50
        scaleOutY.duration = 50
        scaleInX.duration = 150
        scaleInY.duration = 150
        scaleInX.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                binding.fabMenu.menuIconView
                    .setImageResource(if (binding.fabMenu.isOpened) R.drawable.ic_menu else R.drawable.ic_menu_open)
                if (binding.fabMenu.isOpened) binding.fabMenu.menuButtonColorNormal = R.color.primary_color else  binding.fabMenu.menuButtonColorNormal = R.color.black
            }
        })
        set.play(scaleOutX).with(scaleOutY)
        set.play(scaleInX).with(scaleInY).after(scaleOutX)
        set.interpolator = OvershootInterpolator(2.0F)
        binding.fabMenu.iconToggleAnimatorSet = set
    }

    private fun bottomSheet(){
        binding.btnFilter.setOnClickListener {
            val bottomSheet = BottomSheetDialog(this)
            val view = BottomSheetFilterBinding.inflate(layoutInflater)
            bottomSheet.apply {
                view.apply {
                    setContentView(root)
                    show()

                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}