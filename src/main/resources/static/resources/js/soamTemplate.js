document.addEventListener("DOMContentLoaded", (event) =>{
    document.querySelectorAll(".template-item").forEach( (template) => {
        template.addEventListener("click", (e) => {
            const dataset = e.target.dataset;
            document.getElementById("name").value = dataset.tempName;
            document.getElementById("description").value = dataset.tempDescription;
            document.getElementById("notes").value = dataset.tempNotes;
        });
    })
});