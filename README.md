# J-Ledger

Welcome to J-Ledger, a Java implementation of a blockchain Ledger Service. 

## Getting Started

The Ledger Service manages transactions, accounts, and blocks that make up the Blockchain.  Users submit transactions which once validated are added to a block. As Blocks fill up with Transaction objects, account balances are updated. Once 10 transactions are added to a Block, the Block is added to the Ledger. All blocks are chained together by including the hash of the previous block as a field in each new block. The blockchain can be validated at any time by recomputing the hashes of each block and comparing the result with the hash that has been stored in the next block.

## Folder Structure

The workspace contains two folders by default, where:

- `src`: the folder to maintain sources
- `lib`: the folder to maintain dependencies

## Command Syntax

The command syntax specification follows:

- `Create Ledger`: Create a new ledger with the given name, description, and seed value. 
  <br/>```create-ledger <name> description <description> seed <seed>```
- `Create Account`: Create a new account with the given account id. 
  <br/>```create-account <account-id>```
- `Process Transaction`: Process a new transaction.Return an error message if the transaction is invalid, otherwise output the transaction id.
  <br/>```process-transaction <transaction-id> amount <amount> fee <fee> note<note> payer <account-address> receiver <account-address>```
- `Get Account Balance`: Output the account balance for the given account. 
  <br/>```get-account-balance <account-id>```
- `Get Account Balance`: Output the account balances for the most recent completed block.
  <br/>```get-account-balances```
- `Get Block`: Output the details for the given block number.
  <br/>```get-block <block-number>```
- `Get Transaction`: Output the details of the given transaction id. Return an error if the transaction was not found in the Ledger.
  <br/>```get-transaction <transaction-id>```
- `Validate`: Validate the current state of the blockchain.
  <br/>```validate```
 
 Comment lines are denoted with a # in the first column.
