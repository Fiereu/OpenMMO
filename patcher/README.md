# Patcher (OpenMMO)

## ToC
- [Description](#description)
- [How to Run](#how-to-run)
- [How It Works](#how-it-works)
- [Updating for New PokeMMO Public Keys](#updating-for-new-pokemmo-public-keys)
- [Disclaimer](#disclaimer)

## Description
The patcher module is a tool for applying custom patches to the PokeMMO client.
It is designed to automate modifications such as replacing public keys of the PokeMMO servers.
This module is intended for advanced users who understand the risks of modifying game software.

## How to Run

Running is pretty straightforward.

1) Open `gradle.properties` in the root directory of the project and that the following properties are set correctly:
    ```properties
    pokemmo.executable=${HOME}/.local/share/pokemmo/PokeMMO.exe
    pokemmo.workingDir=${HOME}/.local/share/pokemmo/
    ```
2) Make sure you have the right Java Development Kit (JDK) installed. [See JDK Version](../docs/jdk_version.md) for more information.
3) Open a terminal and navigate to the root directory of the project.
4) Run the patcher using Gradle:

| Windows                    | Everything else          |
|----------------------------|--------------------------|
| `gradlew.bat :patcher:run` | `./gradlew :patcher:run` |

## How It Works
The patcher operates by transforming the compiled PokeMMO classes before they are loaded by the Java Virtual Machine (JVM).
For that we use a Java agent that intercepts the class loading process. To do this the patcher registers its `premain` method in the `META-INF/MANIFEST.MF` file of the JAR file.
This method is called by the JVM before any classes are loaded, allowing the patcher to register `ClassFileTransformer` instances that modify the classes as they are loaded.
To parse the binary classes, the patcher uses the [ASM library](https://asm.ow2.io/), which provides an easy-to-use API for reading and writing Java bytecode.

Currently, the patcher only replaces strings in fields and methods that match a predefined pattern.
This way we can replace the public keys used by the PokeMMO client to verify the signatures of packets sent to the server.

### Execution

The patcher is run via tha `run` task in Gradle. This task is a simple `JavaExec` task that runs the PokeMMO client with the patcher agent attached.
The equivalent command would be:
```sh
java -javaagent:patcher/build/libs/patcher.jar -cp path/to/PokeMMO.exe com.pokeemu.client.Client
```

Before running this command, the patcher will automatically all generated public keys from the `:keys` module into the resource directory of the patcher module.
If no keys have been generated, gradle will call the appropriate task to generate them. More infos about that can be found in the [keys module](../keys/README.md).

## Updating for New PokeMMO Public Keys
If the PokeMMO public keys change (for example, after a game update),
update the patcher by replacing the old public key in the `kotlin/de/fiereu/openmmo/patcher/Agent.kt` file with the new ones.

To find the new public keys, you first need to extract the PokeMMO client JAR file from `PokeMMO.exe`.
You can do this by using a tool like [binwalk](https://github.com/ReFirmLabs/binwalk):
```sh
binwalk -e PokeMMO.exe
```

Or by creating a custom extractor that finds the ZIP archive inside the executable. see [ZIP fileformat](https://en.wikipedia.org/wiki/ZIP_(file_format)) for more information.

After extracting the JAR file, you can decompile its classes using a Java decompiler like [JADX](https://github.com/skylot/jadx).
You want to search for the following string: `MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE`.
This string is the start of the base64-encoded public keys used by the PokeMMO client.

You should find a class similar to this:
```java
public abstract class PublicKeys {
    public static final PublicKey getGamePublicKey() {
        return generate(Base64.decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEtqx2myJz3ftlYWgd7cbNqf2t208itQMY7ouPNBDpQetbi7eXbEDxDDZy4Q9fMnI6mF5/D0qMdRd40SRXf0OS7Q=="), "EC");
    }

    public static final PublicKey getChatPublicKey() {
        return generate(Base64.decode("MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEh4Vqgnd+8Fqebu0H40v+FgwhE6RwgAYxJMihb8mJmcHDy8r/rPz3kLHH1oabyKIRUa5Y2cK0TsxZky+mp7DKWA=="), "EC");
    }

    public static boolean verify(byte[] messageBytes, byte[] signatureBytes, PublicKey publicKey, String type) {
        try {
            Signature signature = Signature.getInstance(type);
            signature.initVerify(publicKey);
            signature.update(messageBytes);
            return signature.verify(signatureBytes);
        } catch (Exception ex) {
            System.out.println("Exception verifying " + type + " signature.");
            ex.printStackTrace();
            return false;
        }
    }

    public static PublicKey generate(byte[] bytes, String type) {
        try {
            return KeyFactory.getInstance(type).generatePublic(new X509EncodedKeySpec(bytes));
        } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
```
> Note: this code is cleaned and renamed. The decompiler will probably generate corrupt code and most of the names will differ

## Disclaimer
Using this patcher is against the PokeMMO Terms of Service.
While the risk of being banned is very low, there is no guarantee of safety.
Use at your own risk. The authors are not responsible for any consequences resulting from the use of this tool.
