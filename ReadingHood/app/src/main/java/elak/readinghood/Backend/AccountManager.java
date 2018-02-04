package elak.readinghood.Backend;

import elak.readinghood.Backend.ServerClasses.Request;
import elak.readinghood.Backend.ServerClasses.Update;

import java.util.regex.Pattern;

/**
 * @author Spiros, Nasos
 */
public class AccountManager {
    // input data data
    private String email, username, password, department;

    /**
     * trivial constructor
     */
    public AccountManager() {
        this.email = "";
        this.username = "";
        this.password = "";
        this.department = "";
    }

    /**
     * This function verifies that the usergiven email exists in the database and that the given
     * password is the password of the user, sets the variables email and password if those are
     * correct and returns an error text based on the result of the given variables
     * <p>
     * Error0 = "Success" (which means that the user given variable where correct and then you can
     * user Request class to get the user profile based on these email and password)
     * Error1 = "Wrong Email or Password" (which means that the email does not exist in the databse)
     * Error2 = "Wrong Password" (which means that the given password does not match the email password)
     * Error404 = "Fill every field" (which means that the user must fill all the given fields)
     *
     * @param email    the user given email
     * @param password the user given password
     * @return the error text
     */
    public String login(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            return "Fill every field";
        }

        boolean existenceOfEmail = Request.existenceOfEmail(email.toLowerCase());

        if (existenceOfEmail) {
            boolean correctPassword = Request.checkPasswordForEmail(email.toLowerCase(), password);

            if (correctPassword) {
                this.email = email.toLowerCase();
                this.password = password;

                return "Success";
            } else {
                return "Wrong Password";
            }
        } else {
            return "Wrong Email or Password";
        }
    }

    /**
     * This function assembles the registration process. It uses the functions:
     * registrationSetEMailAndUsername(), registrationSetDepartment(), registrationSetPasswordAndRePassword()
     * and you can check what do they return by looking into their javadoc.
     * <p>
     * For each call of the function you parse the variable that you need and for the rest you put an empty string "".
     * <p>
     * (PanelId 1) = you use this id = 1 to get the results of the registrationSetEMailAndUsername() function.
     * The only variables that you need are email and username and put "" to the rest.
     * <p>
     * (PanelId 2) = you use this id = 2 to set the department registrationSetDepartment() function.
     * The only variable that you need is the department and put "" to the rest.
     * <p>
     * (PanelId 3) = you use this id = 3 to get the results of the registrationSetPasswordAndRePassword() function
     * The only variables that you need are password and rePassword and put "" to the rest.
     *
     * @param panelID    is the id of the panel
     * @param email      is the user given email
     * @param department is the user given department
     * @param username   is the user given username
     * @param password   is the user given password
     * @param rePassword is the user given rePassword
     * @return the error text
     */
    public String registration(int panelID, String email, String username, String department, String password, String rePassword) {
        if (panelID == 1) {
            return registrationSetEMailAndUsername(email, username);
        } else if (panelID == 2) {
            // this function does not return anything
            registrationSetDepartment(department);
        } else if (panelID == 3) {
            return registrationSetPasswordAndRePassword(password, rePassword);
        }
        return "";
    }

    /**
     * This function is initialized ONLY after finishing the setting of the variables of the user.
     * It creates a user.
     *
     * @return a boolean value which indicates if the creation of the user was Successful
     */
    public boolean createUser() {
        String result = Update.createUser(email, username, password, department);
        System.out.println(result);

        if (result.equals("OK")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This function sets the email and the username of the user if the given email is unique
     * and returns an error text based on the result of the given variables.
     * <p>
     * Error0 = "Success" (which means that the email was unique)
     * Error1 = "This email already exists"
     * Error2 = "This isn't and email"
     * Error404 = "Fill every field" (which means that the user must fill all the given fields)
     *
     * @param email    is the user given email
     * @param username is the user given username
     * @return the error text
     */
    private String registrationSetEMailAndUsername(String email, String username) {
        if (email.isEmpty() || username.isEmpty()) {
            return "Fill every field";
        }

        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{1,6}$", Pattern.CASE_INSENSITIVE);
        boolean correctEmailFormat = VALID_EMAIL_ADDRESS_REGEX.matcher(email.toLowerCase()).find();

        if (correctEmailFormat) {
            boolean existenceOfEmail = Request.existenceOfEmail(email);

            if (!existenceOfEmail) {
                this.email = email.toLowerCase();
                this.username = username;
                return "Success";
            } else {
                return "This email already exists";
            }
        } else {
            return "This isn't and email";
        }
    }

    /**
     * This function sets the department of the user. It does not return anything because department is optional
     *
     * @param department is the user given department
     */
    private void registrationSetDepartment(String department) {
        this.department = department;
    }


    /**
     * This function sets the password of the user if the given password meets the format
     * (check the function correctPasswordFormat) of it and returns an error text based
     * on the result of the given variables.
     * <p>
     * <p>
     * Error0 = "Success" (which means that the password meets the password's requirements and
     * the rePassword matches with the password)
     * Error1 = "The password's requirements aren't met"
     * Error2 = "The rePassword doesn't not match"
     * Error404 = "Fill every field" (which means that the user must fill all the given fields)
     *
     * @param password   is the user given password
     * @param rePassword is the user given rePassword
     * @return the error text
     */
    private String registrationSetPasswordAndRePassword(String password, String rePassword) {
        if (password.isEmpty() || rePassword.isEmpty()) {
            return "Fill every field";
        }

        if (correctPasswordFormat(password)) {
            boolean passwordMatchRePassword = password.equals(rePassword);

            if (passwordMatchRePassword) {
                this.password = password;
                return "Success";
            } else {
                return "The rePassword doesn't not match";
            }
        } else {
            return "The password's requirements aren't met";
        }
    }

    /**
     * We assume that the password must contains at least 8 characters and it must include
     * at least one letter (latin only) and at least one number.
     *
     * @return a boolean values which indicates if the password's format is correct
     */
    private boolean correctPasswordFormat(String password) {
        boolean enoughCharacters = (password.length() >= 8);
        boolean letterFound = false;
        boolean numberFound = false;

        for (char c : password.toCharArray()) {
            if (Character.isDigit(c)) {
                numberFound = true;
            }

            if (Character.isLetter(c)) {
                letterFound = true;
            }
        }

        if (enoughCharacters && letterFound && numberFound) {
            return true;
        } else {
            return false;
        }
    }
}