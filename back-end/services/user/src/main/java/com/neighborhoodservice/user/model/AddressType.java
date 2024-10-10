package com.neighborhoodservice.user.model;

/**
 * Types of addresses of a user. Can not be duplicated (e.g. user can not have two home addresses).
 */
public enum AddressType {
    HOME,
    WORK,
    OTHER
}
