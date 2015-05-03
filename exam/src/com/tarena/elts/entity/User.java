package com.tarena.elts.entity;
//entity 实体, 就是业务范畴中的具体名词
import java.io.Serializable;
 
public class User implements Serializable {

  private int id;
  private String name;
  private String passwd;
  private String phone;
  private String email;


  public User() {
    super();
  }

  public User(String name, int id, String passwd) {
    super();
    this.name = name;
    this.id = id;
    this.passwd = passwd;
  }

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getPasswd() {
    return passwd;
  }

  public void setId(int id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPasswd(String passwd) {
    this.passwd = passwd;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }
  
  @Override
  public String toString() {
    return name;
  }
  
  @Override
  public boolean equals(Object obj) {
    if(obj==null)
      return false;
    if(this==obj)
      return true;
    if (obj instanceof User) {
      User other = (User) obj;
      return this.id == other.id;
    }
    return false;
  }
  
  @Override
  public int hashCode() {
    return id;
  }
  

}
