
import http from 'k6/http';
import { check } from 'k6';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const REQUESTS = Number(__ENV.REQUESTS || 100);
const VUS = Number(__ENV.VUS || 10);
const TOKEN = __ENV.TOKEN || '';

export const options = {
    scenarios: {
        analytics: {
            executor: 'shared-iterations',
            vus: VUS,
            iterations: REQUESTS,
            maxDuration: '10m',
        },
    },

    thresholds: {
        http_req_failed: ['rate<0.01'],
        checks: ['rate>0.99'],
    },
};

export default function () {

    const response = http.get(
        `${BASE_URL}/api/v1/analytics?startDate=2026-11-01&endDate=2026-11-07`,
        {
            headers: {
                Authorization: `Bearer ${TOKEN}`,
            },
        }
    );

    check(response, {
        'HTTP 200': (r) => r.status === 200,
        'Response is JSON': (r) =>
            (r.headers['Content-Type'] || '')
                .includes('application/json'),
    });
}
