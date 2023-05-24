package android.coding.ourapp.adapter

import android.coding.ourapp.R
import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter

class ImageSliderAdapter(private val context: Context, private val bitmaps: List<Bitmap>) :
    PagerAdapter() {

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val inflater =
            context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val itemView = inflater.inflate(R.layout.list_item_image, container, false)

        val imageView = itemView.findViewById<ImageView>(R.id.imageView)
        imageView.setImageBitmap(bitmaps[position])

        container.addView(itemView)

        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
        container.removeView(obj as View)
    }

    override fun getCount(): Int {
        return bitmaps.size
    }

    override fun isViewFromObject(view: View, obj: Any): Boolean {
        return view == obj
    }
}