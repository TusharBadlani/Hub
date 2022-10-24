package cessini.technology.commonui.data.amazon

import android.util.Log
import aws.sdk.kotlin.services.sns.SnsClient
import aws.sdk.kotlin.services.sns.createPlatformEndpoint
import aws.sdk.kotlin.services.sns.model.PublishRequest
import cessini.technology.newrepository.preferences.UserIdentifierPreferences
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AmazonSNSImpl @Inject constructor(userIdentifierPreferences: UserIdentifierPreferences) {
    companion object {
        private const val TAG = "AmazonSNSImpl"
    }
    private val userId = userIdentifierPreferences.id
    private var endpoint = ""
    init {
        getEndpoint(userId)
    }



    suspend fun createNewEndpoint(devtoken: String): String {



        Log.d(TAG, "createNewEndpoint: $userId")

        if (endpoint.isEmpty()) {

            try {

//                val endpointRequest = CreatePlatformEndpointRequest {
//                    platformApplicationArn = "arn:aws:sns:ap-south-1:454502946350:app/GCM/MyWorld"
//                    token = devtoken
//                }
                val endPointArn =
                    SnsClient {
                        credentialsProvider = AmazonModule
                        region = "ap-south-1"
                    }.createPlatformEndpoint {
                        platformApplicationArn = "arn:aws:sns:ap-south-1:212833881096:app/GCM/Myworld"
                        token = devtoken
                    }.endpointArn.toString()

                Log.d(TAG, "Endpoint arn $endPointArn")

                addToFirebase(UserArn(userId, devtoken, endPointArn))
                return endPointArn
            } catch (e: Exception) {
                Log.d(TAG, "Error ${e.stackTrace}")
            }
            return endpoint
        } else
            return endpoint

    }

    fun addToFirebase(userArn: UserArn) {
        Firebase.firestore.collection("deviceArn").document("${userArn.userId}").set(userArn)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    Log.d(TAG, "Device added to Firestore ${it.result}")
                } else {
                    Log.d(TAG, "Device added to Firestore ${it.exception}")
                }
            }
    }

    fun getEndpoint(userId: String) {

        Log.d(TAG, "getEndpoint: $userId")
        if (userId.isEmpty())
            return
        Firebase.firestore.collection("deviceArn").document("$userId").get()
            .addOnSuccessListener { doc ->
                endpoint = doc.toObject<UserArn>()?.arn.toString()
                if (endpoint.isNotEmpty())
                    Log.d(TAG, "Device present in Firestore ${doc.data}")
            }
            .addOnFailureListener {
                Log.d(TAG, "Device not present in Firestore ${it.message}")
            }
    }

    suspend fun sendFollowNotification(userId: String) {

        Log.d(TAG, "sendFollowNotification: ")



        Log.d(TAG, "sendFollowNotification: $endpoint")

        if (userId.isEmpty())
            return
        else {
            Firebase.firestore.collection("deviceArn").document("$userId").get()
                .addOnSuccessListener { doc ->
                    val endpoint = doc.toObject<UserArn>()?.arn.toString()
                    if (endpoint.isNotEmpty())
                        Log.d(TAG, "Device present in Firestore ${doc.data}")

                    if (endpoint.isNotEmpty() && endpoint!="null") {

                        val request = PublishRequest {
                            this.messageStructure
                            this.messageAttributes //= Map<String, MessageAttributeValue>("", )
                            this.subject = ""
                            this.message = "You were followed."
                            this.targetArn = endpoint
                            this.subject = "New follower"
                        }
                        Log.e(TAG, "endpoint: $endpoint")
//            var cred = BasicAWSCredentials("AKIAWTUT4SYXBNPNSG4S", "xmtzf8lVqiz+/tH95VCuD3zKf/KOUWszbCg5z6yK")
                        var msg = ""
                        try {
                            GlobalScope.launch {
                                msg = SnsClient {
                                    credentialsProvider = AmazonModule
                                    region = "ap-south-1"
                                }.use { snsClient ->
                                    snsClient.publish(request)
                                }.messageId.toString()

                                Log.d(TAG, "sendFollowNotification: $msg")
                            }
                        } catch (e: Exception) {
                            Log.d(TAG, "sendFollowNotification: ${e.message}")
                        }

                        Log.d(TAG, "amazonsns $msg")
                    } else {
                        Log.d(TAG, "User endpoint not found")
                    }

                }
                .addOnFailureListener {
                    Log.d(TAG, "Device not present in Firestore ${it.message}")
                }
        }

//        if (endpoint.isNotEmpty()) {
//
//            val request = PublishRequest {
//                this.subject = ""
//                this.message = "You were followed."
//                this.targetArn = endpoint
//                this.subject = "New follower"
//            }
////            var cred = BasicAWSCredentials("AKIAWTUT4SYXBNPNSG4S", "xmtzf8lVqiz+/tH95VCuD3zKf/KOUWszbCg5z6yK")
//            var msg = ""
//            try {
//                msg = SnsClient {
//                    credentialsProvider = AmazonModule
//                    region = "ap-south-1"
//                }.use { snsClient ->
//                    snsClient.publish(request)
//                }.messageId.toString()
//            } catch (e: Exception) {
//                Log.d(TAG, "sendFollowNotification: ${e.message}")
//            }
//
//            Log.d(TAG, "amazonsns $msg")
//        }
//        else{
//            Log.d(TAG, "User endpoint not found")
//        }

    }
}

data class UserArn(val userId: String = "", val devtoken: String = "", val arn: String = "")