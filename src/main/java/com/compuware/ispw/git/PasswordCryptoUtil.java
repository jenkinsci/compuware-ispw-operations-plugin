/**
* THESE MATERIALS CONTAIN CONFIDENTIAL INFORMATION AND TRADE SECRETS OF BMC SOFTWARE, INC. YOU SHALL MAINTAIN THE MATERIALS AS
* CONFIDENTIAL AND SHALL NOT DISCLOSE ITS CONTENTS TO ANY THIRD PARTY EXCEPT AS MAY BE REQUIRED BY LAW OR REGULATION. USE,
* DISCLOSURE, OR REPRODUCTION IS PROHIBITED WITHOUT THE PRIOR EXPRESS WRITTEN PERMISSION OF BMC SOFTWARE, INC.
*
* ALL BMC SOFTWARE PRODUCTS LISTED WITHIN THE MATERIALS ARE TRADEMARKS OF BMC SOFTWARE, INC. ALL OTHER COMPANY PRODUCT NAMES
* ARE TRADEMARKS OF THEIR RESPECTIVE OWNERS.
*
* (c) Copyright 2026 BMC Software, Inc.
*/
package com.compuware.ispw.git;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public final class PasswordCryptoUtil
{
	private static final String PREFIX = "enc:gcm:v1:";
	private static final int TAG_BITS = 128;
	private static final String KEY_BASE64 = "9yfqRswQr746fbRtfnsHjz+OCjOzITupz1xT2x9Lvkw=";

	public static String encrypt(String plainText) throws Exception
	{
		if (plainText == null)
		{
			return null;
		}

		byte[] key = Base64.getDecoder().decode(KEY_BASE64);
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");

		byte[] iv = new byte[12];
		new SecureRandom().nextBytes(iv);

		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, new GCMParameterSpec(TAG_BITS, iv));

		byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

		// Split ciphertext and tag (last 16 bytes = tag)
		int tagLength = 16;
		int cipherTextLength = cipherText.length - tagLength;

		byte[] actualCipher = new byte[cipherTextLength];
		byte[] tag = new byte[tagLength];

		System.arraycopy(cipherText, 0, actualCipher, 0, cipherTextLength);
		System.arraycopy(cipherText, cipherTextLength, tag, 0, tagLength);

		return PREFIX + Base64.getEncoder().encodeToString(iv) + ":" + Base64.getEncoder().encodeToString(tag) + ":"
				+ Base64.getEncoder().encodeToString(actualCipher);
	}
}