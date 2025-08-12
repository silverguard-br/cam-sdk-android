# SilverguardCAM (Android)

SDK **Android** para integração com o fluxo de **Contestação CAM** da Silverguard.

---

## 📦 Instalação

### 1) Gradle (GitHub Packages)

> É necessário um **Personal Access Token** do GitHub com escopo **`read:packages`**.  
> Salve suas credenciais em `~/.gradle/gradle.properties`:

```properties
# ~/.gradle/gradle.properties
gpr.user=SEU_USUARIO_GITHUB
gpr.key=SEU_GITHUB_TOKEN_COM_READ_PACKAGES
```

No **settings.gradle** / **settings.gradle.kts** (nível de projeto), adicione o repositório:

### Kotlin DSL (`settings.gradle.kts`)

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/silverguard-br/cam-sdk-android")
            credentials {
                username = providers.gradleProperty("gpr.user").orNull
                    ?: System.getenv("GITHUB_USER")
                password = providers.gradleProperty("gpr.key").orNull
                    ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```
### Groovy (settings.gradle)

```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/silverguard-br/cam-sdk-android")
            credentials {
                username = findProperty("gpr.user") ?: System.getenv("GITHUB_USER")
                password = findProperty("gpr.key")  ?: System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
```

### No módulo app:

```kotlin
dependencies {
    implementation("com.github.silverguard-br:cam:1.0.0")
}
```

## 🧩 Requisitos

- minSdk 24+
- Kotlin e AndroidX habilitados
- Permissões de microfone quando o fluxo solicitar áudio

## 🔐 Permissões (AndroidManifest.xml)

```xml
<uses-permission android:name="android.permission.INTERNET" />

<!-- Áudio (solicitadas em tempo de execução quando necessário) -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />
<uses-permission android:name="android.permission.MODIFY_AUDIO_SETTINGS" />

<!-- Upload de arquivos: só é necessário em Android 12L (API 32) ou inferior -->
<uses-permission
    android:name="android.permission.READ_EXTERNAL_STORAGE"
    android:maxSdkVersion="32" />
```

## 🚀 Uso

### 1) Configuração (Application ou Activity inicial)

```kotlin
import android.app.Application
import com.silverguard.cam.core.config.SilverguardCAM

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        SilverguardCAM.configure(this, "SUA_API_KEY_AQUI")
    }
}
```
> A configuração deve ser feita antes de iniciar qualquer fluxo.

### 2) Inicialização dos fluxos

O SDK oferece dois fluxos principais:

a) Criar uma nova contestação
```kotlin
import com.silverguard.cam.core.config.SilverguardCAM
import com.silverguard.cam.core.model.RequestUrlModel
import java.util.UUID

val request = RequestUrlModel(
    transaction_id = UUID.randomUUID().toString(),
    transaction_amount = 100.0,
    transaction_time = "2025-07-10 11:10:00", // yyyy-MM-dd HH:mm:ss
    transaction_description = "Pagamento via PIX",
    reporter_client_name = "Fulano de Tal",
    reporter_client_id = 12345678901L,
    contested_participant_id = "123456",
    counterparty_client_name = "John Doe",
    counterparty_client_id = 98765432100L,
    counterparty_client_key = "cpf",
    protocol_id = UUID.randomUUID().toString(),
    pix_auto = true,
    client_id = "CLI_456789",
    client_since = "2020-01-15",
    client_birth = "1985-03-22",
    autofraud_risk = true
)

SilverguardCAM.createRequest(this, request) // 'this' = Activity/Context
```

b) Visualizar lista de contestações
```kotlin
import com.silverguard.cam.core.model.RequestListUrlModel

val listModel = RequestListUrlModel(
    reporter_client_id = "12345678901"
)

SilverguardCAM.getRequests(this, listModel)
```

### 3) Exemplos de modelos enviados

RequestUrlModel (utilizado em novas contestações)
```kotlin
RequestUrlModel(
    transaction_id = "abc123",
    transaction_amount = 150.0,
    transaction_time = "2025-07-11 11:10:00",
    transaction_description = "Pagamento via PIX",
    reporter_client_name = "John Doe",
    reporter_client_id = 123456789L,
    contested_participant_id = "123456",
    counterparty_client_name = "Maria dos Santos",
    counterparty_client_id = 987654321L,
    counterparty_client_key = "DEST_KEY_1",
    protocol_id = "PROT_2025_001",
    pix_auto = true,
    client_id = "CLI_456789",
    client_since = "2020-01-15",
    client_birth = "1985-03-22",
    autofraud_risk = true
)
```

RequestListUrlModel (utilizado na listagem de contestações)
```kotlin
RequestListUrlModel(
    reporter_client_id = "12345678901"
)
```
### 4) Captura de retorno com CAMSdkNavigator

Implemente a interface para ser notificado quando o usuário aciona voltar dentro do fluxo.
O SDK enviará um origin (string) indicando de qual tela/etapa o retorno ocorreu e finaliza a Activity do fluxo após o callback.
```kotlin
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.silverguard.cam.core.navigator.CAMSdkNavigator

class MainActivity : AppCompatActivity(), CAMSdkNavigator {
    override fun onBackFromCAMSdk(origin: String?) {
        Toast.makeText(this, "Voltou do fluxo: ${origin ?: "desconhecido"}", Toast.LENGTH_SHORT).show()
        // A Activity do fluxo já foi finalizada pelo SDK.
    }
}
```

### 5) Exemplo completo (Activity de exemplo)
```kotlin
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.silverguard.cam.core.config.SilverguardCAM
import com.silverguard.cam.core.model.RequestListUrlModel
import com.silverguard.cam.core.model.RequestUrlModel
import com.silverguard.cam.core.navigator.CAMSdkNavigator
import java.util.UUID

class MainActivity : AppCompatActivity(), CAMSdkNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configure antes de usar qualquer fluxo
        SilverguardCAM.configure(this, "SUA_API_KEY_AQUI")

        // Criar contestação
        findViewById<Button>(R.id.btn_create_request).setOnClickListener {
            val request = RequestUrlModel(
                transaction_id = UUID.randomUUID().toString(),
                transaction_amount = 150.0,
                transaction_time = "2025-07-11 11:10:00",
                transaction_description = "Pagamento via PIX",
                reporter_client_name = "John Doe",
                reporter_client_id = 123456789L,
                contested_participant_id = "123456",
                counterparty_client_name = "Maria dos Santos",
                counterparty_client_id = 987654321L,
                counterparty_client_key = "DEST_KEY_1",
                protocol_id = "PROT_2025_001",
                pix_auto = true,
                client_id = "CLI_456789",
                client_since = "2020-01-15",
                client_birth = "1985-03-22",
                autofraud_risk = true
            )
            SilverguardCAM.createRequest(this, request)
        }

        // Listar contestações
        findViewById<Button>(R.id.btn_get_requests_list).setOnClickListener {
            val requestList = RequestListUrlModel(
                reporter_client_id = "12345678901"
            )
            SilverguardCAM.getRequests(this, requestList)
        }
    }

    override fun onBackFromCAMSdk(origin: String?) {
        Toast.makeText(this, "Comando 'back' vindo da $origin", Toast.LENGTH_SHORT).show()
    }
}
```

## 📄 Licença
Este SDK é distribuído sob a licença proprietária da Silverguard. O uso é restrito a clientes autorizados.
