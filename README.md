# Desktop AI Companion

A desktop AI companion built with JavaFX and an embedded web interface. It provides a floating chat window, animated visual feedback, and AI responses powered by the Qwen API.

## Features

- **Floating desktop window** — A transparent, borderless window that stays on top of other applications.
- **Draggable interface** — Move the assistant by dragging the animated orb.
- **AI chat** — Send text prompts and display responses from Qwen.
- **Animated feedback** — The orb switches between idle and thinking animations.
- **Asynchronous requests** — Model requests run asynchronously to avoid blocking the UI while waiting for a response.

## Tech Stack

- **Java / JavaFX** — Application lifecycle, desktop window management, and UI thread coordination.
- **FXML / WebView** — Window layout and embedded web content.
- **HTML / CSS / JavaScript** — Chat interface and state animations.
- **JSObject / WebEngine** — Communication between Java and JavaScript.
- **HttpClient / CompletableFuture** — Asynchronous HTTP requests and response handling.
- **Maven** — Dependency management and build configuration.
- **Qwen API** — Cloud-based text generation through Alibaba Cloud Model Studio.

## How It Works

1. The user enters a message in the web interface.
2. JavaScript calls the Java controller through a registered bridge.
3. The Java HTTP client sends an asynchronous request to the Qwen API.
4. The response is passed back to the JavaFX Application Thread.
5. Java invokes a JavaScript function to display the reply and restore the idle animation.

```text
Web Interface
      |
      | JavaScript → Java
      v
JavaFX Controller
      |
      | Asynchronous HTTP request
      v
Qwen API
      |
      | Response → JavaFX UI thread
      v
Chat Bubble + Idle Animation
```

## Getting Started

### Requirements

- JDK 11 or later
- Maven
- An internet connection
- An Alibaba Cloud Model Studio API key with access to the configured model

The project uses JavaFX 18.0.2 and targets Java 11.

### Setup

1. Clone the repository:

   ```bash
   git clone https://github.com/jiuzhangmiles66-cloud/desktopPet.git
   cd desktopPet
   ```

2. Locate the directory containing `pom.xml`. If it is inside a `desktoppet` subdirectory, enter that directory:

   ```bash
   cd desktoppet
   ```

3. Configure the API key in:

   ```text
   src/main/java/com/tang/OllamaClient.java
   ```

   Replace the `YOUR_API_KEY` placeholder with your own key for local use. Do not commit or share a real API key.

   The current configuration uses:

   - Model: `qwen-turbo`
   - Endpoint: `https://dashscope-intl.aliyuncs.com/compatible-mode/v1/chat/completions`

4. From the directory containing `pom.xml`, run:

   ```bash
   mvn clean javafx:run
   ```

### Usage

- Drag the orb to move the desktop window.
- Enter a message and click the send button.
- The orb displays a thinking animation while the request is pending.
- The reply appears in the chat bubble.

Launch the JavaFX application to use desktop features. Opening `index.html` directly in a browser does not provide the Java bridge required for chat and window control.

## Project Structure

Paths below are relative to the directory containing `pom.xml`.

```text
src/main/
├── java/
│   ├── module-info.java
│   └── com/tang/
│       ├── App.java
│       ├── PrimaryController.java
│       ├── OllamaClient.java
│       └── SecondaryController.java
└── resources/com/tang/
    ├── index.html
    ├── primary.fxml
    └── secondary.fxml
```

- `App.java` — Creates and displays the desktop window.
- `PrimaryController.java` — Connects the web interface to Java and handles window dragging.
- `OllamaClient.java` — Sends requests to the cloud Qwen API.
- `index.html` — Defines the chat interface, orb styling, and animations.

## Current Limitations

- Each request contains only the current message; conversation history is not included.
- Responses are displayed after completion rather than streamed token by token.
- JSON handling and network error recovery are basic and could be improved.
- The startup code still attempts to launch local Ollama, but the current chat implementation uses the cloud Qwen API. Local Ollama is not required for that chat request path.

## Future Improvements

- Load API credentials from environment variables.
- Replace manual JSON parsing with a dedicated JSON library.
- Add request timeouts and clearer error recovery.
- Support conversation history and streaming responses.
- Unify local and cloud model configuration.
