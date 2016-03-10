# Event Specifications #



## General Events ##
Each event has the following mandatory properties:
  * Application ID : UUID
> > The unique id of the application they belong to.
  * Control Flow ID : int
> > The application unique id of the control flow the events belong to. This id is used to identify responses to events currently blocking the application control flow.

### GenericSuccessEvent ###

| **Property** | **Value** |
|:-------------|:----------|
| ApplicationId | `<UUID>`  |
| ControlFlowId | `<int>`   |
| RefObject    | `<object-id>` |

### ExceptionEvent ###

| **Property** | **Value** |
|:-------------|:----------|
| ApplicationId | `<UUID>`  |
| ControlFlowId | `<int>`   |

## Socket Events ##

### SocketBoundEvent ###

| **Property** | **Value** |
|:-------------|:----------|
| ApplicationId | `<UUID>`  |
| ControlFlowId | `<int>`   |
| SocketRef    | `<socket-id>` |
| address      | `<SocketAddress>` |

### SocketBindRequestEvent ###

Caused by:
  * `java.net.Socket#bind(SocketAddress)`

Successful Response:
  * [SocketBoundEvent](#SocketBoundEvent.md)

Exceptions:
  * IOException

| **Property** | **Value** |
|:-------------|:----------|
| ApplicationId | `<UUID>`  |
| ControlFlowId | `<int>`   |
| SocketRef    | `<socket-id>` |
| bindpoint    | `<SocketAddress>` |

### SocketClosedEvent ###

Caused by:
  * `java.net.Socket#close()`

| **Property** | **Value** |
|:-------------|:----------|
| ApplicationId | `<UUID>`  |
| ControlFlowId | `<int>`   |
| SocketRef    | `<socket-id>` |

### SocketConnectRequestEvent ###

Caused by:
  * `java.net.Socket#connect(SocketAddress)`

| **Expected Response:** | [GenericSuccessEvent](#GenericSuccessEvent.md) |
|:-----------------------|:-----------------------------------------------|
| **Expected Error:**    | [SocketExceptionEvent](#SocketExceptionEvent.md) |
| **Property**           | **Value**                                      |
| ApplicationId          | `<UUID>`                                       |
| ControlFlowId          | `<int>`                                        |
| SocketRef              | `<socket-id>`                                  |
| remoteAddress          | `<SocketAddress>`                              |
| timeoutInMillis        | `<int>`                                        |

### SocketSetKeepAliveEvent ###

Caused by:
  * `java.net.Socket#setKeepAlive(boolean)`

| **Expected Response:** | [GenericSuccessEvent](#GenericSuccessEvent.md) |
|:-----------------------|:-----------------------------------------------|
| **Expected Error:**    | [SocketExceptionEvent](#SocketExceptionEvent.md) |
| **Property**           | **Value**                                      |
| ApplicationId          | `<UUID>`                                       |
| ControlFlowId          | `<int>`                                        |
| SocketRef              | `<socket-id>`                                  |
| enableKeepAlive        | `<boolean>`                                    |

### SocketSetOOBInlineEvent ###

Caused by:
  * `java.net.Socket#setOOBInline(boolean)`

| **Expected Response:** | [GenericSuccessEvent](#GenericSuccessEvent.md) |
|:-----------------------|:-----------------------------------------------|
| **Expected Error:**    | [SocketExceptionEvent](#SocketExceptionEvent.md) |
| **Property**           | **Value**                                      |
| ApplicationId          | `<UUID>`                                       |
| ControlFlowId          | `<int>`                                        |
| SocketRef              | `<socket-id>`                                  |
| enabled                | `<boolean>`                                    |

### SocketSetPerformancePreferencesEvent ###

Caused by:
  * `java.net.Socket#setPerformancePreferences(int, int ,int)`

| **Expected Response:** | [GenericSuccessEvent](#GenericSuccessEvent.md) |
|:-----------------------|:-----------------------------------------------|
| **Expected Error:**    | [SocketExceptionEvent](#SocketExceptionEvent.md) |
| **Property**           | **Value**                                      |
| ApplicationId          | `<UUID>`                                       |
| ControlFlowId          | `<int>`                                        |
| SocketRef              | `<socket-id>`                                  |
| connectionTime         | `<int>`                                        |
| latency                | `<int>`                                        |
| bandwidth              | `<int>`                                        |

### SocketSetReceiveBufferSizeEvent ###

Caused by:
  * `java.net.Socket#setReceiveBufferSize(int)`

| **Expected Response:** | [GenericSuccessEvent](#GenericSuccessEvent.md) |
|:-----------------------|:-----------------------------------------------|
| **Expected Error:**    | [SocketExceptionEvent](#SocketExceptionEvent.md) |
| **Property**           | **Value**                                      |
| ApplicationId          | `<UUID>`                                       |
| ControlFlowId          | `<int>`                                        |
| SocketRef              | `<socket-id>`                                  |
| bufferSize             | `<int>`                                        |

### SocketSetReuseAddressEvent ###

Caused by:
  * `java.net.Socket#setReuseAddress(boolean)`

| **Expected Response:** | [GenericSuccessEvent](#GenericSuccessEvent.md) |
|:-----------------------|:-----------------------------------------------|
| **Expected Error:**    | [SocketExceptionEvent](#SocketExceptionEvent.md) |
| **Property**           | **Value**                                      |
| ApplicationId          | `<UUID>`                                       |
| ControlFlowId          | `<int>`                                        |
| SocketRef              | `<socket-id>`                                  |
| enabled                | `<boolean>`                                    |

### SocketSetSoLingerEvent ###

Caused by:
  * `java.net.Socket#setSoLinger(boolean, int)`

| **Expected Response:** | [GenericSuccessEvent](#GenericSuccessEvent.md) |
|:-----------------------|:-----------------------------------------------|
| **Expected Error:**    | [SocketExceptionEvent](#SocketExceptionEvent.md) |
| **Property**           | **Value**                                      |
| ApplicationId          | `<UUID>`                                       |
| ControlFlowId          | `<int>`                                        |
| SocketRef              | `<socket-id>`                                  |
| enabled                | `<boolean>`                                    |
| linger                 | `<int>`                                        |

### SocketSetTimeoutEvent ###

Caused by:
  * `java.net.Socket#setTimeout( int)`

| **Expected Response:** | [GenericSuccessEvent](#GenericSuccessEvent.md) |
|:-----------------------|:-----------------------------------------------|
| **Expected Error:**    | [SocketExceptionEvent](#SocketExceptionEvent.md) |
| **Property**           | **Value**                                      |
| ApplicationId          | `<UUID>`                                       |
| ControlFlowId          | `<int>`                                        |
| SocketRef              | `<socket-id>`                                  |
| timeoutInMillis        | `<int>`                                        |

### SocketSetTcpNoDelayEvent ###

Caused by:
  * `java.net.Socket#setTcpNoDelay(boolean)`

| **Expected Response:** | [GenericSuccessEvent](#GenericSuccessEvent.md) |
|:-----------------------|:-----------------------------------------------|
| **Expected Error:**    | [SocketExceptionEvent](#SocketExceptionEvent.md) |
| **Property**           | **Value**                                      |
| ApplicationId          | `<UUID>`                                       |
| ControlFlowId          | `<int>`                                        |
| SocketRef              | `<socket-id>`                                  |
| enabled                | `<boolean>`                                    |

### SocketSetTrafficClassEvent ###

Caused by:
  * `java.net.Socket#setTrafficClass(int)`

| **Expected Response:** | [GenericSuccessEvent](#GenericSuccessEvent.md) |
|:-----------------------|:-----------------------------------------------|
| **Expected Error:**    | [SocketExceptionEvent](#SocketExceptionEvent.md) |
| **Property**           | **Value**                                      |
| ApplicationId          | `<UUID>`                                       |
| ControlFlowId          | `<int>`                                        |
| SocketRef              | `<socket-id>`                                  |
| trafficClass           | `<int>`                                        |