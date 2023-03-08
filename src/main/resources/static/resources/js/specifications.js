const TREE_ICON_MAP = {
    'specification': 'bi bi-file-text',
    'specificationObjective': 'bi bi-bullseye',
    'stakeholder': 'bi bi-person-check-fill',
    'stakeholderObjective': 'bi bi-check-circle'
};
const DEFAULT_MESSAGE = 'Please select an item in Entity Explorer';

function createTreeEntity(item) {
    return {
        'text': item.name,
        'icon': TREE_ICON_MAP[item.type],
        'data': {
            'type': item.type,
            'id': item.id
        }
    };
}

function jsTreeDataLoader(obj, callback) {
    if (obj.id === '#') {
        callback([{
            'text': 'Specifications',
            'state': {
                'opened': true
            },
            'data': {
                'type': 'specifications'
            },
            'children': true
        }]);
    } else {
        switch (obj.data.type) {
            case 'specifications':
                fetch('/tree/specification')
                    .then(response => response.json())
                    .then(items => {
                        const children = items.map(item => {
                            const treeEntity = createTreeEntity(item);
                            treeEntity.children = [
                                {
                                    'text': 'Objectives',
                                    'data': {
                                        'type': 'specificationObjectives',
                                        'specificationId': item.id
                                    },
                                    'children': true
                                },
                                {
                                    'text': 'Stakeholders',
                                    'data': {
                                        'type': 'stakeholders',
                                        'specificationId': item.id
                                    },
                                    'children': true
                                }
                            ];
                            return treeEntity;
                        });
                        callback(children);
                    });
                break;
            case 'specificationObjectives':
                fetch('/tree/specification/' + obj.data.specificationId + '/specificationObjective')
                    .then(response => response.json())
                    .then(items => {
                        const children = items.map(createTreeEntity);
                        callback(children);
                    });
                break;
            case 'stakeholders':
                fetch('/tree/specification/' + obj.data.specificationId + '/stakeholder')
                    .then(response => response.json())
                    .then(items => {
                        const children = items.map(item => {
                            const treeEntity = createTreeEntity(item);
                            treeEntity.children = [{
                                'text': 'Stakeholder Objectives',
                                'data': {
                                    'type': 'stakeholderObjectives',
                                    'stakeholderId': item.id
                                },
                                'children': true
                            }];
                            return treeEntity;
                        });
                        callback(children);
                    });
                break;
            case 'stakeholderObjectives':
                fetch('/tree/stakeholder/' + obj.data.stakeholderId + '/stakeholderObjective')
                    .then(response => response.json())
                    .then(items => {
                        const children = items.map(createTreeEntity);
                        callback(children);
                    });
                break;
        }
    }
}

function loadMainPanel(url) {
    fetch(url)
        .then(response => response.text())
        .then(text => $('#main').html(text));
}

$(document).ready(function () {
    loadMainPanel('/specifications2/default');

    $('#tree').jstree({
        'core': {
            'data': jsTreeDataLoader
        }
    });


    /*{
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
    });*/
    $('#tree').on('activate_node.jstree', (e, data) => {
        console.log("activate_node: data: " + data);
        let url;
        if (data.node.data.id) {
            url = '/' + data.node.data.type + '2/' + data.node.data.id + '/edit';
            for (let i = 1; i < data.node.parents.length - 1; i += 2) {
                const parentData = $('#tree').jstree().get_node(data.node.parents[i]).data;
                url = '/' + parentData.type + '/' + parentData.id + url;
            }
        } else {
            url = '/specifications2/default';
        }
        loadMainPanel(url);
    });
})

function soamFormSubmit(form) {
    const action = form.action;
    fetch(form.action, {
        method: form.method,
        body: new FormData(form)
    })
        .then(response => response.text())
        .then(text => {
            $('#main').html(text);
            $('#tree').jstree().refresh();
        });
}