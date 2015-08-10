#!/usr/bin/env python

# Dictionary to store user information
_USERS = {}

# User class for storing walking information
class User():

    def __init__(self, username):
        self.username = username
        self.xVal = []
        self.yVal = []
        self.zVal = []


def create_user(username):
    user = User(username)
    _USERS[username] = user
    return user

def get_user(username):
    try:
        return _USERS[username]
    except KeyError:
        if noerror:
            return None
        else:
            raise KeyError
