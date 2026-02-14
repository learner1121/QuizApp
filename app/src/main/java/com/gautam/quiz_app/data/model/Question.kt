import com.google.gson.annotations.SerializedName

data class Question(
        @SerializedName("_id") val _id: String? = null,
        @SerializedName("questionText") val questionText: String?,
        @SerializedName("section") val section: String?,
        @SerializedName("options") val options: List<String>,
        @SerializedName("correctAnswer") val correctAnswer: String?,
        @SerializedName("marks") val marks: Int?
)
