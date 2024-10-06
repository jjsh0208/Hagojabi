document.addEventListener("DOMContentLoaded", function (){
    const passwordInput = document.getElementById("password");
    const confirmPasswordInput = document.getElementById("confirmPassword");
    const form = document.getElementById("registration-form");

    form.addEventListener("submit", function (event){
        if (passwordInput.value !== confirmPasswordInput.value){
            event.preventDefault();
            alert("비일번호가 일치하지 않습니다.");
            confirmPasswordInput.focus();
        }
    });
});