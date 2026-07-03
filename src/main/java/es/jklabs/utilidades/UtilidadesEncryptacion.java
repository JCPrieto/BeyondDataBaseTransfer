package es.jklabs.utilidades;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class UtilidadesEncryptacion {

    private static final String LEGACY_INIT_VECTOR = "5AAA6aC_funj3E#S";
    private static final String LEGACY_KEY = "UqebTGVj&f%8%SUR";
    private static final String VERSION_PREFIX = "v2";
    private static final String ACTIVE_KEY_ID = "202607";
    private static final int GCM_IV_LENGTH_BYTES = 12;
    private static final int GCM_TAG_LENGTH_BITS = 128;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Map<String, String> KEYS = new HashMap<>();

    static {
        KEYS.put(ACTIVE_KEY_ID, "BeyondDataBaseTransfer:GCM:202607:6E8F7B0D8F2C4A90");
    }

    private UtilidadesEncryptacion() {

    }

    public static String encrypt(String value) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
            RANDOM.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, getCurrentKey(), new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
            byte[] encrypted = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
            byte[] payload = ByteBuffer.allocate(iv.length + encrypted.length)
                    .put(iv)
                    .put(encrypted)
                    .array();
            return VERSION_PREFIX + ":" + ACTIVE_KEY_ID + ":" + Base64.getEncoder().encodeToString(payload);
        } catch (Exception ex) {
            Logger.error("Encriptar dato", ex);
        }

        return null;
    }

    public static String decrypt(String encrypted) {
        try {
            if (encrypted != null && encrypted.startsWith(VERSION_PREFIX + ":")) {
                return decryptCurrent(encrypted);
            }
            return decryptLegacy(encrypted);
        } catch (Exception ex) {
            Logger.error("Desencriptar dato", ex);
        }

        return null;
    }

    private static String decryptCurrent(String encrypted) throws Exception {
        String[] parts = encrypted.split(":", 3);
        if (parts.length != 3) {
            throw new IllegalArgumentException("Formato cifrado no valido");
        }
        SecretKeySpec key = getKey(parts[1]);
        byte[] payload = Base64.getDecoder().decode(parts[2]);
        if (payload.length <= GCM_IV_LENGTH_BYTES) {
            throw new IllegalArgumentException("Payload cifrado no valido");
        }
        ByteBuffer buffer = ByteBuffer.wrap(payload);
        byte[] iv = new byte[GCM_IV_LENGTH_BYTES];
        byte[] ciphertext = new byte[payload.length - GCM_IV_LENGTH_BYTES];
        buffer.get(iv);
        buffer.get(ciphertext);
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
        byte[] original = cipher.doFinal(ciphertext);
        return new String(original, StandardCharsets.UTF_8);
    }

    private static String decryptLegacy(String encrypted) throws Exception {
        IvParameterSpec iv = new IvParameterSpec(LEGACY_INIT_VECTOR.getBytes(StandardCharsets.UTF_8));
        SecretKeySpec skeySpec = new SecretKeySpec(LEGACY_KEY.getBytes(StandardCharsets.UTF_8), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
        byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));

        return new String(original, StandardCharsets.UTF_8);
    }

    private static SecretKeySpec getCurrentKey() throws Exception {
        return getKey(ACTIVE_KEY_ID);
    }

    private static SecretKeySpec getKey(String keyId) throws Exception {
        String key = KEYS.get(keyId);
        if (key == null) {
            throw new IllegalArgumentException("Clave de cifrado no encontrada: " + keyId);
        }
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return new SecretKeySpec(digest.digest(key.getBytes(StandardCharsets.UTF_8)), "AES");
    }
}
