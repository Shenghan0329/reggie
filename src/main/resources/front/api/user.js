function findUser(phone) {
    return $axios({
        'url': '/user/'+phone,
        'method': 'get',
    })
}

function updateUser(data) {
    return $axios({
        'url': '/user',
        'method': 'put',
        data
    })
}