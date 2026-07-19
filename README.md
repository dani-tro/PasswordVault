# Password Vault

A client-server password manager written in Java for the Modern Java Technologies course at FMI, Sofia University. The server handles authentication, storage and password-strength checks for multiple clients at once; the client is a command-line tool for registering, logging in and managing saved credentials per site.

## What it does

- Runs multiple client connections concurrently, one thread per client.
- Hashes account passwords before storing them and encrypts per-site passwords so they can be decrypted and shown back to the user later.
- Checks new passwords against the [Enzoic Passwords API](https://www.enzoic.com/docs-passwords-api/) before saving them, using a partial-hash lookup so the actual password never leaves the client in full.
- Can generate a strong random password for a site instead of you making one up.
- Logs users out automatically after a minute of inactivity.

Commands from the client:
| Command | Description |
|---|---|
| `register <user> <password> <password-repeat>` | Create a new account |
| `login <user> <password>` | Authenticate |
| `logout` | End the current session |
| `add-password <website> <user> <password>` | Store a password for a site (validated against the Enzoic API) |
| `generate-password <website> <user>` | Generate and store a strong password for a site |
| `retrieve-credentials <website> <user>` | Fetch stored credentials for a site |
| `remove-password <website> <user>` | Delete stored credentials for a site |
| `disconnect` | Close the connection to the server |
| `help` | List available commands |


## How it's structured

Client and server both use the command pattern - `Command.of(...)` on the client, `ServerCommand.of(...)` on the server so each action (login, add-password, etc.) is its own class instead of a big switch statement. New command = new class, nothing else has to change.

Packages:
- `algorithm` - hashing and AES encryption
- `client` - CLI, socket I/O, client commands
- `communication` - the request/response objects sent over the socket
- `exception` - custom checked exceptions
- `server` - the socket loop, one command class per action, the Enzoic API client, per-user vault storage

Client and server talk over plain sockets using Java object serialization (`ObjectInputStream`/`ObjectOutputStream`).

## Running it

You'll need JDK 17+ and an Enzoic API key/secret if you want the compromised-password check to actually work (get one [here](https://www.enzoic.com/free-trial-2/)). Set them as environment variables before starting the server:

```bash
export ENZOIC_API_KEY=your_key
export ENZOIC_API_SECRET=your_secret
```

Compile and run:

```bash
mkdir -p out
find src -name "*.java" > sources.txt
javac -d out @sources.txt

java -cp out bg.sofia.uni.fmi.mjt.server.Server
```

and in another terminal:

```bash
java -cp out bg.sofia.uni.fmi.mjt.client.Client
```

The client connects to `localhost:7354` by default - that's a constant in `Server`/`NetworkManager` if you want to change it.

Tests are JUnit 5 + Mockito, easiest run from an IDE. From the command line you'd need the JUnit console launcher on your classpath alongside Mockito.

## Errors

The client shows short, plain-English messages for expected stuff (bad login, invalid command, etc.). Anything unexpected gets logged with a full stack trace to `log.txt` on whichever side it happened, instead of exposing internal details to the end user.
