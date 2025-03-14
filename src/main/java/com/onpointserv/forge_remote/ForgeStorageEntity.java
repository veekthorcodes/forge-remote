package com.onpointserv.forge_remote;

record ForgeStorageEntity() {
    record Node(String key, Object value) {
    }

    record Edge(Node node) {
    }

    record SetInput(String key, Object value, boolean encrypted) {
    }

    record DeleteInput(String key) {
    }
}
