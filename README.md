# NIS
Tool to allow users to chat and transfer files securely amongst each other.

To initlize chatting between two clients do the following:
1. Open 3 terminals
2. In the first terminal, run MultiThreadChatServer
3. In the second terminal, run MultiThreadChatClient
4. Enter your name for the chat & you will be added to the chat
5. Repeat step 3 & 4 in the third terminal
6. Your chat has been established.

NB**Here we are using AES 256 bits running in CBC mode.
The good thing about using AES instead of DES is that it already stops plain text attacks which could be made by users.

