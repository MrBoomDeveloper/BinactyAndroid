#include <jni.h>
#include <eos_sdk.h>
#include <eos_auth.h>

extern "C" JNIEXPORT void JNICALL
Java_com_mrboomdev_binacty_online_lobby_LobbyAuth_signInGoogle(
        JNIEnv *env,
        jobject thiz,
        jstring login,
        jstring password,
        jobject callback
) {
    EOS_Auth_LoginOptions loginOptions = EOS_Auth_LoginOptions();
    loginOptions.ApiVersion = EOS_AUTH_CREDENTIALS_API_LATEST;

    EOS_Auth_Credentials credentials = EOS_Auth_Credentials();
    credentials.ApiVersion = EOS_AUTH_LOGIN_API_LATEST;
    credentials.Type = EOS_ELoginCredentialType::EOS_LCT_ExternalAuth;
    credentials.ExternalType = EOS_EExternalCredentialType::EOS_ECT_GOOGLE_ID_TOKEN;

    credentials.Token = env->GetStringUTFChars(password, JNI_FALSE);
    credentials.Id = env->GetStringUTFChars(login, JNI_FALSE);

    EOS_HAuth auth = EOS_HAuth();

    EOS_Auth_Login(auth, &loginOptions, {}, {});
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_mrboomdev_binacty_online_BinactyOnline_init(
        __attribute__((unused)) JNIEnv *env,
        __attribute__((unused)) jobject thiz
) {
    EOS_InitializeOptions SDKOptions = { 0 };
    SDKOptions.ApiVersion = EOS_INITIALIZE_API_LATEST;
    SDKOptions.ProductName = "Binacty Engine";
    SDKOptions.ProductVersion = "0.1";

    EOS_EResult InitResult = EOS_Initialize(&SDKOptions);

    if(InitResult != EOS_EResult::EOS_Success){
        return JNI_FALSE;
    }

    return JNI_TRUE;
}