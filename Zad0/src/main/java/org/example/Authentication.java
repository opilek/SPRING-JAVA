package org.example;

import org.apache.commons.codec.digest.DigestUtils;

public class Authentication
{
    private IUserRepository repo;
    private User loggedUser = null;

    public User getLoggedUser() {return loggedUser;}

    public Authentication(IUserRepository repo)
    {
        this.repo = repo;
    }

    public User authenticate(String login, String password)
    {

        User user = repo.getUser(login);

        if(user == null)
        {
            return null;
        }

        String hasshedPassword = DigestUtils.sha256Hex(password);

        if(user.getPassword().equals(hasshedPassword))
        {
            this.loggedUser = user;
            return user;
        }
        return null;

    }
    public static String hashPassword(String password)
    {
        return DigestUtils.sha256Hex(password);
    }

    public void loggout()
    {
        this.loggedUser = null;
        System.out.println("Sesja została zakończona");
    }
}
