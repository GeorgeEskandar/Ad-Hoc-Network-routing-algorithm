# Ad-Hoc-Network-routing-algorithm
Implement two routing protocols for Ad-Hoc networks : flooding and DSR
August 2018

An ad-hoc routng protocol is implemented for a network with 5 raspberry pi. Two main algorithms are applied:

Flooding algorithm: 
- The senderNode file is uploaded on the node that wants to send (or discover the network) 
- The Node file is uploaded on other nodes

Flooding with Dynamic Source Routing:
-DSRSenderNode# is uploaded to raspberry pi number # when it is the sender
- otherwise it executes the code DSRNode#
A packet begins with a flag (a letter) that indicates its function:
- F means route discovery or flooding (broadcast)
- R means a reply to the route discovery (intended destination node initiates the reply) (no broadcast)
- M is a message packet intended to a certain node (no broadcast)
