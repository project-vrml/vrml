package chatroom

import (
	"fmt"
	"github.com/gorilla/websocket"
	"log"
	"net/http"
)

var clients = make(map[*websocket.Conn]bool) // connected clients
var broadcast = make(chan Message)           // broadcast channel

// Configure the upgrader
var upgrader = websocket.Upgrader{
	CheckOrigin: func(r *http.Request) bool {
		return true
	},
}

// Define our message object
type Message struct {
	Email    string `json:"email"`
	Username string `json:"username"`
	Message  string `json:"message"`
}

func main() {
	// Create a simple file server
	fs := http.FileServer(http.Dir("../resources/public"))
	http.Handle("/", fs)

	// Configure websocket route
	http.HandleFunc("/ws", handleConnections)

	// Start listening for incoming chat messages
	go handleMessages()

	// Start the server on localhost port 8000 and log any errors
	log.Println("http server started on :8000")
	err := http.ListenAndServe(":8000", nil)
	if err != nil {
		log.Fatal("ListenAndServe: ", err)
	}
}

func handleConnections(w http.ResponseWriter, r *http.Request) {
	// Upgrade initial GET request to a websocket
	ws, err := upgrader.Upgrade(w, r, nil)
	if err != nil {
		log.Fatal(err)
	}
	// Make sure we close the connection when the function returns
	defer ws.Close()

	// Register our new client
	clients[ws] = true

	for {
		var msg Message
		// Read in a new message as JSON and map it to a Message object
		err := ws.ReadJSON(&msg)
		if err != nil {
			log.Printf("error: %v", err)
			delete(clients, ws)
			break
		}
		// Send the newly received message to the broadcast channel
		broadcast <- msg
	}
}

func handleMessages() {
	for {
		// Grab the next message from the broadcast channel
		msg := <-broadcast
		// Send it out to every client that is currently connected
		for client := range clients {
			err := client.WriteJSON(msg)
			if err != nil {
				log.Printf("error: %v", err)
				client.Close()
				delete(clients, client)
			}
		}
	}
}

// ------------------------------------------------------------------ go error case

// 使用error的写法
func first() error  { return nil }
func second() error { return nil }
func third() error  { return nil }
func fourth() error { return nil }
func fifth() error  { return nil }

func Do() error {
	var err error
	if err = first(); err == nil {
		if err = second(); err == nil {
			if err = third(); err == nil {
				if err = fourth(); err == nil {
					if err = fifth(); err == nil {
						return nil
					}
				}
			}
		}
	}
	return err
}

// panic 写法
func first2()  {}
func second2() {}
func third2()  {}
func fourth2() {}
func fifth2()  {}

func Do2() (err error) {
	defer func() {
		if r := recover(); r != nil {
			err = fmt.Errorf("Error: %+v", r)
		}
	}()
	first2()
	second2()
	third2()
	fourth2()
	fifth2()
	return
}
