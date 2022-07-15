import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class encryptDecrypt {

  public static final String ALGORITHM = "RSA";

  // Chave privada.
  public static final String PATH_CHAVE_PRIVADA = "C:/keys/private.txt";

  // Chave pública.
  public static final String PATH_CHAVE_PUBLICA = "C:/keys/public.txt";

  public static void geraChave() {
    try {
      final KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ALGORITHM);
      keyGen.initialize(1024);
      final KeyPair key = keyGen.generateKeyPair();

      File chavePrivadaFile = new File(PATH_CHAVE_PRIVADA);
      File chavePublicaFile = new File(PATH_CHAVE_PUBLICA);

      // Criando os arquivos para guardar a chave Privada e Publica
      if (chavePrivadaFile.getParentFile() != null) {
        chavePrivadaFile.getParentFile().mkdirs();
      }

      chavePrivadaFile.createNewFile();

      if (chavePublicaFile.getParentFile() != null) {
        chavePublicaFile.getParentFile().mkdirs();
      }

      chavePublicaFile.createNewFile();

      // Salva a Chave Pública
      ObjectOutputStream chavePublicaOS = new ObjectOutputStream(
          new FileOutputStream(chavePublicaFile));
      chavePublicaOS.writeObject(key.getPublic());
      chavePublicaOS.close();

      // Salva a Chave Privada
      ObjectOutputStream chavePrivadaOS = new ObjectOutputStream(
          new FileOutputStream(chavePrivadaFile));
      chavePrivadaOS.writeObject(key.getPrivate());
      chavePrivadaOS.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  // Confirmano se par de chaves Pública e Privada já foram geradas.
  public static boolean verificaSeExisteChavesNoSO() {

    File chavePrivada = new File(PATH_CHAVE_PRIVADA);
    File chavePublica = new File(PATH_CHAVE_PUBLICA);

    if (chavePrivada.exists() && chavePublica.exists()) {
      return true;
    }
    return false;
  }

  // Criptografa o texto puro usando chave pública.
  public static byte[] criptografa(String texto, PublicKey chave) {
    byte[] cipherText = null;

    try {
      final Cipher cipher = Cipher.getInstance(ALGORITHM);
      // Criptografa o texto puro usando a chave Púlica
      cipher.init(Cipher.ENCRYPT_MODE, chave);
      cipherText = cipher.doFinal(texto.getBytes());
    } catch (Exception e) {
      e.printStackTrace();
    }

    return cipherText;
  }

  // Descriptografa o texto puro usando chave privada.
  public static String decriptografa(byte[] texto, PrivateKey chave) {
    byte[] dectyptedText = null;

    try {
      final Cipher cipher = Cipher.getInstance(ALGORITHM);
      // Descriptografa o texto puro usando a chave Privada
      cipher.init(Cipher.DECRYPT_MODE, chave);
      dectyptedText = cipher.doFinal(texto);

    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return new String(dectyptedText);
  }

  // Testa o Algoritmo
  public static void main(String[] args) {

    try {

      // Vvai verifica se existe um par de chaves, caso contrário vai gerar
      if (!verificaSeExisteChavesNoSO()) {
        geraChave();
      }

      final String msgOriginal = "Texto de exemplo para teste";
      ObjectInputStream inputStream = null;

      // Criptografa usando a Chave Pública
      inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PUBLICA));

      final PublicKey chavePublica = (PublicKey) inputStream.readObject();
      final byte[] textoCriptografado = criptografa(msgOriginal, chavePublica);

      // Descriptografa a Mensagem usando a Chave Pirvada
      inputStream = new ObjectInputStream(new FileInputStream(PATH_CHAVE_PRIVADA));
      final PrivateKey chavePrivada = (PrivateKey) inputStream.readObject();
      final String textoPuro = decriptografa(textoCriptografado, chavePrivada);

      // Imprime o texto original, o texto criptografado e
      // o texto descriptografado.
      System.out.println("Texto Original: " + msgOriginal);
      System.out.println("Texto Criptografada: " + textoCriptografado.toString());
      System.out.println("Texto Descriptografada: " + textoPuro);

    } catch (Exception e) {
      e.printStackTrace();
    }

  }

}