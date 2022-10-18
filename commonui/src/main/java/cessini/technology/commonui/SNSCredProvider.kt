package cessini.technology.commonui

import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.auth.awscredentials.CredentialsProvider
import aws.smithy.kotlin.runtime.time.Instant

object AmazonModule : CredentialsProvider {
    override suspend fun getCredentials(): Credentials {
        return Credentials("AKIA2HSZ2STD2WB2M4FI", "UafFAPi6G9dBMTktmN+DhNJJNE9CWRzC50mj7s83")
    }
}
