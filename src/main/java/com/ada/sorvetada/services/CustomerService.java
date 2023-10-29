package com.ada.sorvetada.services;

import com.ada.sorvetada.dtos.CustomerDto;
import com.ada.sorvetada.entities.Customer;
import com.ada.sorvetada.entities.Role;
import com.ada.sorvetada.repositories.CustomerRepository;

import com.ada.sorvetada.repositories.RoleRepository;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.springframework.core.StandardReflectionParameterNameDiscoverer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public CustomerService(CustomerRepository customerRepository,
                           PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public List<Customer> getAll() {
        return customerRepository.findAll();
    }

    public List<Customer> getAllActive() {
        boolean active = true;
        return customerRepository.findByActive(active);
    }


    public CustomerDto getById(Long id) {
        Optional<Customer> optCustomer = customerRepository.findById(id);
        Customer customer = optCustomer.orElseThrow(() -> new RuntimeException("Does not exist customer by id"));
        return createNewCustomer(customer);
    }

    public CustomerDto getByEmail(String email) {
        Optional<Customer> optCustomer = customerRepository.findByEmail(email);
        Customer customer = optCustomer.orElseThrow(() -> new RuntimeException("Does not exist customer by email"));
        return createNewCustomer(customer);
    }


    public CustomerDto createNewCustomer(Customer customer) {
        return CustomerDto.builder().id(customer.getId()).name(customer.getName()).cpf(customer.getCpf()).email(customer.getEmail()).password(customer.getPassword()).active(customer.isActive()).build();
    }

    public List<Customer> getByName(String name) {
        return customerRepository.findByName(name);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    public void activateDisableCustomer(boolean active, Long id) {
        customerRepository.activeUser(active, id);
    }

    /*    public CustomerDto saveCustumer(CustomerDto customerDTO) {
            Customer customer = new Customer(customerDTO.getName(),
                    customerDTO.getCpf(), customerDTO.getEmail(), passwordEncoder.encode(customerDTO.getPassword()),
                    customerDTO.isActive());
            Customer savedCustomer = customerRepository.save(customer);
            return createNewCustomer(savedCustomer);
        }*/
    public CustomerDto saveCustumer(CustomerDto customerDTO) {
        Customer customer = new Customer(customerDTO.getName(), customerDTO.getCpf(), customerDTO.getEmail(), passwordEncoder.encode(customerDTO.getPassword()), customerDTO.isActive());
        Customer savedCustomer = customerRepository.save(customer);
        return createNewCustomer(savedCustomer);
    }

    public CustomerDto updateCustomer(CustomerDto customerDTO) {
        Optional<Customer> optCustomer = customerRepository.findById(customerDTO.getId());
        Customer customer = optCustomer.orElseThrow(() -> new RuntimeException("Doesn't exist client by id"));
        if (customerDTO.getEmail() != null && !customerDTO.getEmail().isEmpty())
            customerDTO.setEmail(customerDTO.getEmail());
        if (customerDTO.getName() != null && !customerDTO.getName().isEmpty()) customer.setName(customerDTO.getName());
        Customer savedCustomer = customerRepository.save(customer);
        return createNewCustomer(savedCustomer);
    }

    public ResponseEntity<String> updatePassword(Long id, HashMap<String, String> password) {
        String activePassword = (password.get("active"));
        String newPassword = (password.get("new"));
        String confirmationPassword = (password.get("confirmation"));
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        Customer customer = optionalCustomer.orElseThrow(() -> new RuntimeException("Customer id not found!"));
        if (passwordEncoder.matches(activePassword, customer.getPassword())) {
            if (newPassword.equals(confirmationPassword)) {
                customer.setPassword(passwordEncoder.encode(newPassword));
                customerRepository.save(customer);
                return new ResponseEntity<>("Update completed", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("New password and confirmation is not equals", HttpStatus.UNPROCESSABLE_ENTITY);
            }

        } else {
            return new ResponseEntity<>("Invalid password", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseEntity<String> insertRole(Long idCustomer, Long idRole) {
        Optional<Customer> optionalCustomer = customerRepository.findById(idCustomer);
        Customer customer = optionalCustomer.orElseThrow(() -> new RuntimeException("Customer id not found"));
        Optional<Role> optionalRole = roleRepository.findById(idRole);
        Role role = optionalRole.orElseThrow(() -> new RuntimeException("Role id not found!"));
        customer.getRoles().add(role);
        customerRepository.save(customer);
        return new ResponseEntity<>("Role add successful", HttpStatus.OK);
    }

    public CustomerDto authenticate(String email, String password) {
        // Busque o cliente com base no email
        Optional<Customer> optCustomer = customerRepository.findByEmail(email);

        if (optCustomer.isPresent()) {
            Customer customer = optCustomer.get();

            // Verifique se a senha fornecida corresponde à senha armazenada
            if (customer.getPassword().equals(password)) {
                return createNewCustomer(customer);
            }
        }

        // Se o email e senha não corresponderem, retorne null ou lance uma exceção adequada
        return null;
    }
}

