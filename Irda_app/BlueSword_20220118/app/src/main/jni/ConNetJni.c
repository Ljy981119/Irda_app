#define __STDC_WANT_LIB_EXT1__ 1
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <malloc.h>
//引入上面生成的头文件，并实现头文件中声明的方法
#include "com_example_myapplication_mynative_ConNetJni.h"




JNIEXPORT jstring JNICALL Java_com_example_myapplication_mynative_ConNetJni_sayHello(JNIEnv *env, jobject obj){//方法名是Java_包名_类名_方法名
    char *str = "来了";

    return (*env)->NewStringUTF(env, str);
}

JNIEXPORT jstring JNICALL Java_com_example_myapplication_mynative_ConNetJni_getHexSixteen(JNIEnv *env,jobject obj,jint jnum){//方法名是Java_包名_类名_方法名





int num =jnum;

 printf("\n c-int: %d",num);
    	//int num = 0;
    	int a[100];
    	int i = 0;
    	int m = 0;
    	int yushu=0;
    	char hex[16] = { '0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F' };
    	static char buf[200];

    	while (num > 0)
    	{
    		yushu = num % 16;
    		a[i++] = yushu;
    		num= num / 16;

    }

    	for (i = i - 1; i >= 0; i--)//倒序输出
    	{
    		m = a[i];
    		 printf(buf,"%02hhu\n", hex[m]);

    	}

   return (*env)->NewStringUTF(env,buf);
}


JNIEXPORT jstring JNICALL Java_com_example_myapplication_mynative_ConNetJni_getcdHexDecimal(JNIEnv *env,jobject obj,jstring gstring){//方法名是Java_包名_类名_方法名


   const char * code = (char *)(*env)->GetStringUTFChars(env,gstring, 0);
    printf("\n c-string: hello - %s", code);

     char* str;
	long i = strtol(code, &str, 16);

	static char buf[20];

	 printf(buf,"%02ld\n", i);
      return (*env)->NewStringUTF(env,buf);
  }







