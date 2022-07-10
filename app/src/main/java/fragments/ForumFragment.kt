package fragments

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.wotapp.NewPostForumActivity
import com.example.wotapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import java.util.HashMap

class ForumFragment : Fragment() {
    private lateinit var addPostButton: FloatingActionButton
    private lateinit var postTable: TableLayout
    private lateinit var category: Spinner
    private lateinit var categorySpinnerAdapter: ArrayAdapter<CharSequence>
    private val firestore = FirebaseFirestore.getInstance()
    private var postList = HashMap<String, QuerySnapshot>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_forum, container, false)
        addPostButton = view.findViewById(R.id.addPostButton)
        postTable = view.findViewById(R.id.postsTable)
        category = view.findViewById(R.id.categorySpiner)
        categorySpinnerAdapter = activity?.let { ArrayAdapter.createFromResource(it,
            com.example.wotapp.R.array.category,
            com.example.wotapp.R.layout.spinner_category) } as ArrayAdapter<CharSequence>
        category.adapter = categorySpinnerAdapter

        addPostButton.setOnClickListener {
            val intent = Intent(activity, NewPostForumActivity::class.java)
            startActivity(intent)
        }
        val categoriesArray = resources.getStringArray(R.array.category)
        for(categoryy in categoriesArray){
            getPostsFromFirebase(categoryy)
        }


        return view
    }

    private fun initializeView(){
        if(postList.size == 4) {
            for (documents in postList){
                for (document in documents.value) {
                    val tableRow = TableRow(activity)
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT,1.0f)
                    tableRow.layoutParams = params
                    tableRow.isClickable = true
                    tableRow.background = resources.getDrawable(R.drawable.white_border)
                    tableRow.setOnClickListener { Toast.makeText(activity, "Docuemnt XDXDXD",
                        Toast.LENGTH_SHORT).show() }

                    val firstLayout = LinearLayout(context)
                    firstLayout.orientation = LinearLayout.VERTICAL
                    val titlePost = TextView(activity)
                    titlePost.setTextColor(Color.WHITE)
                    titlePost.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                    titlePost.textSize = 20.0f
                    titlePost.text = document.data["title"] as CharSequence?

                    val categoryPost = TextView(activity)
                    categoryPost.layout
                    categoryPost.setTextColor(Color.GRAY)
                    categoryPost.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
                    categoryPost.textSize = 18.0f
                    categoryPost.text = document.data["date"] as CharSequence?

                    val creatorPost = TextView(activity)
                    creatorPost.setTextColor(Color.GRAY)
                    creatorPost.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
                    creatorPost.textSize = 18.0f
                    creatorPost.text = document.data["nickname"] as CharSequence?

                    val secondLayout = LinearLayout(context)
                    secondLayout.orientation = LinearLayout.VERTICAL

                    firstLayout.background = resources.getDrawable(R.drawable.white_border)
                    secondLayout.background = resources.getDrawable(R.drawable.white_border)
                    firstLayout.addView(creatorPost)
                    firstLayout.addView(categoryPost)
                    secondLayout.addView(titlePost)
                    tableRow.addView(firstLayout,300,250)
                    tableRow.addView(secondLayout ,720,250)
                    postTable.addView(tableRow)
                }
            }
        }

    }

    private fun getPostsFromFirebase(category:String){
        firestore.collection("forumPosts").document(category)
            .collection("post").get()
            .addOnSuccessListener { documents ->
                postList[category] = documents
                initializeView()
            }
            .addOnFailureListener {
                Toast.makeText(activity, "Problem with creating post. Check your internet connection",
                    Toast.LENGTH_SHORT).show()
            }
    }

}