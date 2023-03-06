$(document).ready(function () {
    $('#tree').jstree(/*{
        'plugins': ['contextmenu'],
        'contextmenu': {
            'items': (node, callback) => {
                switch (node.data.type) {
                    case 'specifications':
                        callback({
                            'new': {
                                'label': ''
                            }
                        });
                        break;
                }
            }
        }
    }*/);
    $('#tree').on('activate_node.jstree', (e, data) => {
        console.log("activate_node: data: " + data);
        if (data.node.data.id) {
            let url = '/' + data.node.data.type + '2/' + data.node.data.id + '/edit';
            for (let i = 1; i < data.node.parents.length - 1; i += 2) {
                const parentData = $('#' + data.node.parents[i]).data();
                url = '/' + parentData.type + '/' + parentData.id + url;
            }
            fetch(url)
                .then(response => response.text())
                .then(text => $('#main').html(text));
        } else {
            $('#main').html('');
        }
    });
})

function soamFormSubmit(form) {
    const action = form.action;
    fetch(form.action, {
        method: form.method,
        body: new FormData(form)
    })
        .then(response => response.text())
        .then(text => $('#main').html(text));
}