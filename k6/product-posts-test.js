import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    vus: 100,
    duration: '30s',
};

export default function () {
    const page = Math.floor(Math.random() * 5);

    const url = `http://localhost:8080/zero9/product-posts?progressStatus=DOING&page=${page}&size=10`;

    const res = http.get(url);

    check(res, {
        'status is 200': (r) => r.status === 200,
        'response time < 500ms': (r) => r.timings.duration < 500,
    });

    sleep(1);
}