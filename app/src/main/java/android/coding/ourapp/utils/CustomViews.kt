@file:Suppress("RemoveExplicitTypeArguments", "RemoveExplicitTypeArguments",
    "RemoveExplicitTypeArguments", "RemoveExplicitTypeArguments", "RemoveExplicitTypeArguments",
    "RemoveExplicitTypeArguments", "RemoveExplicitTypeArguments"
)

package android.coding.ourapp.utils
import android.net.Uri
import io.ak1.pix.models.Flash
import io.ak1.pix.models.Mode
import io.ak1.pix.models.Options
import io.ak1.pix.models.Ratio

internal const val TAG = "Pix logs"

val options = Options().apply{
    ratio = Ratio.RATIO_AUTO                                    //Image/video capture ratio
    count = 3                                                //Number of images to restrict selection count
    spanCount = 4                                               //Number for columns in grid
    path = "media/gallery"                                         //Custom Path For media Storage
    isFrontFacing = false                                       //Front Facing camera on start
    mode = Mode.Picture                                           //Option to select only pictures or videos or both
    flash = Flash.Auto                                          //Option to select flash type
    preSelectedUrls = ArrayList<Uri>()                          //Pre selected Image Urls

}

//fun fragmentBody(
//    context: Context,
//    adapter: RecyclerView.Adapter<android.coding.ourapp.adapter.MultipleImageAdapter.ViewHolder>,
//    clickCallback: View.OnClickListener
//): View {
//    val layoutParams = FrameLayout.LayoutParams(
//        FrameLayout.LayoutParams.MATCH_PARENT,
//        FrameLayout.LayoutParams.MATCH_PARENT
//    ).apply {
//        this.gravity = Gravity.RIGHT or Gravity.BOTTOM
//    }
//    return FrameLayout(context).apply {
//        this.layoutParams = layoutParams
//        addView(RecyclerView(context).apply {
//            layoutManager = GridLayoutManager(context, 3)
//            setPadding(0, 100, 0, 0)
//            this.layoutParams = layoutParams
//            this.adapter = adapter
//        })
//        addView(FloatingActionButton(context).apply {
//            this.layoutParams = FrameLayout.LayoutParams(
//                ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT
//            ).apply {
//                setMargins(32, 32, 32, 32)
//                this.gravity = Gravity.RIGHT or Gravity.BOTTOM
//            }
//            imageTintList = ColorStateList.valueOf(Color.WHITE)
//            setImageResource(R.drawable.ic_date)
//            setOnClickListener(clickCallback)
//        })
//    }
//}
//
//fun fragmentBody2(
//    context: Context,
//    adapter: RecyclerView.Adapter<android.coding.ourapp.adapter.MultipleImageAdapter.ViewHolder>,
//): View {
//    val layoutParams = LinearLayout.LayoutParams(
//        FrameLayout.LayoutParams.MATCH_PARENT,
//        FrameLayout.LayoutParams.MATCH_PARENT
//    ).apply {
//        this.gravity = Gravity.RIGHT or Gravity.BOTTOM
//        weight = 1f
//    }
//    return LinearLayout(context).apply {
//        orientation = LinearLayout.VERTICAL
//        this.layoutParams = layoutParams
//        addView(Space(context).apply {
//            this.layoutParams = LinearLayout.LayoutParams(
//                FrameLayout.LayoutParams.MATCH_PARENT,
//                context.toPx(100f).toInt()
//            )
//        })
//        addView(RecyclerView(context).apply {
//            layoutManager = GridLayoutManager(context, 3)
//            setPadding(0, 100, 0, 0)
//            this.layoutParams = layoutParams
//            this.adapter = adapter
//        })
//    }
//}